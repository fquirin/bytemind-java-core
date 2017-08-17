package de.bytemind.core.databases;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.bytemind.core.tools.Connectors;
import de.bytemind.core.tools.EsQueryBuilder;
import de.bytemind.core.tools.JSON;
import de.bytemind.core.tools.Timer;

public class ElasticsearchTestSequence {

	public static void main(String[] args) {

		//Setup
		String endpoint = "http://localhost:21312";
		ElasticSearchConfig.setEndpoint(endpoint);
		Elasticsearch elastic = new Elasticsearch();

		//Test path
		String index = "storage";
		String type = "userData";
		
		//Test item
		String userId1 = "uid0815";
		String email1 = "user1@example.com";
		JSONObject data1 = JSON.make("user", userId1, "info", "The first user", "email", email1, 
				"list", JSON.makeArray("Item1", "Item2"), "map", JSON.make("type", "test1", "items", 2));
		String userId2 = "uid1234";
		String email2 = "user2@example.com";
		JSONObject data2 = JSON.make("user", userId2, "info", "The second user", "email", email2, 
				"list", JSON.makeArray("Item11", "Item12", "Item13"), "map", JSON.make("type", "test2", "items", 3));
		
		//Clear index
		System.out.println("---Clear index (try)---");
		JSONObject res = elastic.deleteAnything(index);
		JSON.printJSONpretty(res);		//if (JSON.getIntegerOrDefault(res, "code", -1) != 0){return;}
		
		//Add mapping
		System.out.println("\n---Add mapping to index---");
		res = Elasticsearch.putMapping(endpoint, index, JSON.readJsonFromFile("Settings//Elasticsearch//test-mapping.json"));
		JSON.printJSONpretty(res);		if (JSON.getIntegerOrDefault(res, "code", -1) != 0){return;}
		
		//Add data
		System.out.println("\n---Add user1 data---");
		res = elastic.setAnyItemData(index, type, data1);
		JSON.printJSONpretty(res);		if (JSON.getIntegerOrDefault(res, "code", -1) != 0){return;}
		String id = JSON.getString(res, "_id");
		
		System.out.println("\n---Add user2 data---");
		res = elastic.setAnyItemData(index, type, data2);
		JSON.printJSONpretty(res);		if (JSON.getIntegerOrDefault(res, "code", -1) != 0){return;}
		
		//give some time to refresh
		System.out.println("\n---Wait a sec.---");
		Timer.threadSleep(1500);
		
		//Get data
		System.out.println("\n---Get user1 data---");
		res = elastic.getItem(index, type, id);
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
				
		//Query data simple
		System.out.println("\n---Query (simple) user1 data---");
		res = elastic.searchSimple(index + "/" + type, "user:uid0815");
		JSONArray hits = JSON.getJArray(res, new String[]{"hits", "hits"});
		JSONObject print = JSON.getJObject((JSONObject) hits.get(0), "_source");
		System.out.println("HITS: " + hits.size() + " - FIRST HIT: ");
		JSON.printJSONpretty(print);		if (!Connectors.httpSuccess(res)){return;}
		
		//Query data json
		System.out.println("\n---Query (json) user2 data---");
		String query = EsQueryBuilder.getBoolMustMatch(EsQueryBuilder.makeQueryList("user", userId2)).toJSONString();
		res = elastic.searchByJson(index + "/" + type, query);
		hits = JSON.getJArray(res, new String[]{"hits", "hits"});
		print = JSON.getJObject((JSONObject) hits.get(0), "_source");
		System.out.println("HITS: " + hits.size() + " - FIRST HIT: ");
		JSON.printJSONpretty(print);		if (!Connectors.httpSuccess(res)){return;}
		
		//Query data json - nested
		System.out.println("\n---Query (json) user2 data nested ---");
		query = EsQueryBuilder.getBoolMustMatch(EsQueryBuilder.makeQueryList("map.items", 3)).toJSONString();
		res = elastic.searchByJson(index + "/" + type, query);
		hits = JSON.getJArray(res, new String[]{"hits", "hits"});
		print = JSON.getJObject((JSONObject) hits.get(0), "_source");
		System.out.println("HITS: " + hits.size() + " - FIRST HIT: ");
		JSON.printJSONpretty(print);		if (!Connectors.httpSuccess(res)){return;}
		
		//Query data json - special characters
		System.out.println("\n---Query (json) special characters ---");
		query = EsQueryBuilder.getBoolMustMatch(EsQueryBuilder.makeQueryList("email", email1)).toJSONString();
		res = elastic.searchByJson(index + "/" + type, query);
		hits = Elasticsearch.getHits(res);
		print = (hits.size() > 0)? JSON.getJObject((JSONObject) hits.get(0), "_source") : (new JSONObject());
		System.out.println("HITS: " + hits.size() + " - FIRST HIT: ");
		JSON.printJSONpretty(print);		if (!Connectors.httpSuccess(res)){return;}
		
		//Query data json - multiple hits
		System.out.println("\n---Query (json) multi hit ---");
		query = EsQueryBuilder.getBoolMustMatch(EsQueryBuilder.makeQueryList("info", "the")).toJSONString();
		res = elastic.searchByJson(index + "/" + type, query);
		hits = Elasticsearch.getHits(res);
		print = (hits.size() > 0)? JSON.getJObject((JSONObject) hits.get(0), "_source") : (new JSONObject());
		System.out.println("HITS: " + hits.size() + " - FIRST HIT: ");
		JSON.printJSONpretty(print);		if (!Connectors.httpSuccess(res)){return;}
		
		//Check size
		System.out.println("\n---Check number of documents---");
		res = Elasticsearch.customGET(endpoint, index + "/" + type, "_count", "");
		//res = elastic.customGET(index + "/" + type, "_search", URLBuilder.getString("", "?q=", "*", "&size=", "2"));
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Delete data by query
		System.out.println("\n---Delete user2 by query---");
		query = EsQueryBuilder.getBoolMustMatch(EsQueryBuilder.makeQueryList("info", "second")).toJSONString();
		res = elastic.deleteByJson(index + "/" + type, query);
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Delete data by id
		System.out.println("\n---Delete user1 by id---");
		res = elastic.deleteItem(index, type, id);
		JSON.printJSONpretty(res);		if (JSON.getIntegerOrDefault(res, "code", -1) != 0){return;}
		
		//give some time to refresh
		System.out.println("\n---Wait a sec.---");
		Timer.threadSleep(1500);
		
		//Check size
		System.out.println("\n---Check number of documents---");
		res = Elasticsearch.customGET(endpoint, index + "/" + type, "_count", "");
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Clear index
		System.out.println("\n---Clear index---");
		res = elastic.deleteAnything(index);
		JSON.printJSONpretty(res);		if (JSON.getIntegerOrDefault(res, "code", -1) != 0){return;}
		
		System.out.println("\n--- ALL DONE :-) ---");
	}

}
