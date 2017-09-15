package de.bytemind.core.databases;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.bytemind.core.server.Statistics;
import de.bytemind.core.tools.Connectors;
import de.bytemind.core.tools.Debugger;
import de.bytemind.core.tools.JSON;

/**
 * Class to access Elasticsearch. Implements the interface for knowledge databases.
 * 
 * @author Florian Quirin
 *
 */
public class Elasticsearch implements KnowledgeDatabase {
	final private static String API_NAME = "Elasticsearch";
	
	//ElasticSearch address
	String server = ElasticSearchConfig.getEndpoint();
	
	/**
	 * Default constructor. Reads server end-point from configuration 'ElasticSearchConfig'.
	 */
	public Elasticsearch() {};
	/**
	 * Manual constructor. Overwrites server end-point URL (effectively ignoring configuration).
	 */
	public Elasticsearch(String server) {
		this.server = server;
	};
	
	//-------INTERFACE IMPLEMENTATIONS---------
	
	@Override
	public boolean testConnection() {
		JSONObject test = customGET(ElasticSearchConfig.getEndpoint(), "","","");
		if (Connectors.httpSuccess(test)){
			return test.containsKey("cluster_uuid");
		}else{
			return false;
		}
	}
	
	//SET
	public JSONObject setItemData(String index, String type, String item_id, JSONObject data) {
		int resCode = writeDocument(index, type, item_id, data);
		JSONObject res = null;
		if (resCode == 0){
			res = JSON.make("result", "success", "code", resCode);
		}else{
			res = JSON.make("result", "fail", "code", resCode);
			if (resCode == 1){
				JSON.put(res, "error", "no connection to DB or internal error");
			}
		}
		return res;
	}
	public JSONObject setAnyItemData(String index, String type, JSONObject data) {
		return writeDocument(index, type, data);
	}
	//GET
	public JSONObject getItem(String index, String type, String item_id) {
		return getDocument(index, type, item_id);
	}
	public JSONObject getItemFiltered(String index, String type, String item_id, String[] filters) {
		//convert filters to sources-string
		String sources = "";
		for (String f : filters){
			sources += f.trim() + ",";
		}
		sources = sources.replaceFirst(",$", "").trim();
		return getDocument(index, type, item_id, sources);
	}
	//UPDATE
	public JSONObject updateItemData(String index, String type, String item_id, JSONObject data) {
		int resCode = updateDocument(index, type, item_id, data);
		JSONObject res = null;
		if (resCode == 0){
			res = JSON.make("result", "success", "code", resCode);
		}else{
			res = JSON.make("result", "fail", "code", resCode);
			if (resCode == 1){
				JSON.put(res, "error", "no connection to DB or internal error");
			}
		}
		return res;
	}
	//SEARCH SIMPLE
	public JSONObject searchSimple(String path, String search_term){		
		//Build URL
		if (!path.endsWith("/")) { path = path + "/"; }
		try{
			String url = server + "/" + path + "_search?q=" + URLEncoder.encode(search_term, "UTF-8");
		
			long tic = System.currentTimeMillis();
			JSONObject result = Connectors.httpGET(url);
			//System.out.println(result.toJSONString()); 		//debug
			
			//success?
			if (Connectors.httpSuccess(result)){
				Statistics.addInternalApiHit(API_NAME + ":" + "searchSimple", tic);
				return result;
			}
			//error
			else{
				Statistics.addInternalApiHit(API_NAME + ":" + "searchSimple" + "-error", tic);
				return result;
			}
		//error
		}catch (Exception e){
			JSONObject res = new JSONObject();
			JSON.add(res, "error", "request failed! - e: " + e.getMessage());
			JSON.add(res, "code", -1);
			return res;
		}
	}
	//SEARCH COMPLEX
	public JSONObject searchByJson(String path, String jsonQuery) {
		if (!path.endsWith("/")) { path = path + "/"; }
		try{
			String url = server + "/" + path + "_search";
			//System.out.println("url: " + url); 		//debug
			//System.out.println("query: " + jsonQuery); 		//debug
			long tic = System.currentTimeMillis();
			JSONObject result = Connectors.httpPOST(url, jsonQuery);
			//System.out.println(result.toJSONString()); 		//debug
			
			//success?
			if (Connectors.httpSuccess(result)){
				Statistics.addInternalApiHit(API_NAME + ":" + "searchByJson", tic);
				return result;
			}
			//error
			else{
				Statistics.addInternalApiHit(API_NAME + ":" + "searchByJson" + "-error", tic);
				return result;
			}
		//error
		}catch (Exception e){
			JSONObject res = new JSONObject();
			JSON.add(res, "error", "request failed! - e: " + e.getMessage());
			JSON.add(res, "code", -1);
			return res;
		}
	}
	//DELETE
	public JSONObject deleteItem(String index, String type, String item_id) {
		int resCode = deleteDocument(index, type, item_id);
		JSONObject res = null;
		if (resCode == 0){
			res = JSON.make("result", "success", "code", resCode);
		}else{
			res = JSON.make("result", "fail", "code", resCode);
			if (resCode == 1){
				JSON.put(res, "error", "no connection to DB or internal error");
			}
		}
		return res;
	}
	public JSONObject deleteAnything(String path) {
		int resCode = deleteAny(path);
		JSONObject res = null;
		if (resCode == 0){
			res = JSON.make("result", "success", "code", resCode);
		}else{
			res = JSON.make("result", "fail", "code", resCode);
			if (resCode == 1){
				JSON.put(res, "error", "no connection to DB or internal error");
			}
		}
		return res;
	}
	//DELETE COMPLEX
	public JSONObject deleteByJson(String path, String jsonQuery) {
		if (!path.endsWith("/")) { path = path + "/"; }
		try{
			String url = server + "/" + path + "_delete_by_query";
			//System.out.println("url: " + url); 		//debug
			//System.out.println("query: " + jsonQuery); 		//debug
			long tic = System.currentTimeMillis();
			JSONObject result = Connectors.httpPOST(url, jsonQuery);
			//System.out.println(result.toJSONString()); 		//debug
			
			//success?
			if (Connectors.httpSuccess(result)){
				Statistics.addInternalApiHit(API_NAME + ":" + "deleteByJson", tic);
				return result;
			}
			//error
			else{
				Statistics.addInternalApiHit(API_NAME + ":" + "deleteByJson" + "-error", tic);
				return result;
			}
		//error
		}catch (Exception e){
			JSONObject res = new JSONObject();
			JSON.add(res, "error", "request failed! - e: " + e.getMessage());
			JSON.add(res, "code", -1);
			return res;
		}
	}
	
	//--------ELASTICSEARCH METHODS---------
	
	/**
	 * Write document at "id" of "type" in "index".
	 * @param index - index name, e.g. "account"
	 * @param type - type name, e.g. "user"
	 * @param id - id name/number, e.g. user_id
	 * @param data - JSON data to put inside id
	 * @return error code (0 - no error, 1 - no connection or fail)
	 */
	public int writeDocument(String index, String type, String id, JSONObject data){		
		//PUT headers
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Length", Integer.toString(data.toJSONString().getBytes().length));
		
		//Build URL
		String url = server + "/" + index + "/" + type + "/" + id;
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpPUT(url, data.toJSONString(), headers);
		//System.out.println(result.toJSONString()); 		//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "writeDocument", tic);
			return 0;
		}
		//error
		else{
			Debugger.println("writeDocument - ElasticSearch - error in '" + index + "/" + type + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "writeDocument" + "-error", tic);
			return 1;
		}
	}
	/**
	 * Write document at random id of "type" in "index".
	 * @param index - index name, e.g. "account"
	 * @param type - type name, e.g. "user"
	 * @param data - JSON data to put inside id
	 * @return JSON with error "code" (0 - no error, 1 - no connection or fail) and "_id" if created
	 */
	public JSONObject writeDocument(String index, String type, JSONObject data){		
		//POST headers
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Length", Integer.toString(data.toJSONString().getBytes().length));
		
		//Build URL
		String url = server + "/" + index + "/" + type;
		//System.out.println("writeDocument URL: " + url); 		//debug
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpPOST(url, data.toJSONString(), headers);
		//System.out.println("writeDocument Result: " + result.toJSONString()); 				//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "writeDocument", tic);
			return JSON.make("code", 0, "_id", result.get("_id"));
		}
		//error
		else{
			Debugger.println("writeDocument - ElasticSearch - error in '" + index + "/" + type + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "writeDocument" + "-error", tic);
			return JSON.make("code", 1);
		}
	}
	
	/**
	 * Update or create document at "id" of "type" in "index".
	 * @param index - index name, e.g. "account"
	 * @param type - type name, e.g. "user"
	 * @param id - id name/number, e.g. user_id
	 * @param data - JSON data to put inside id
	 * @return error code (0 - no error, 1 - no connection or fail)
	 */
	public int updateDocument(String index, String type, String id, JSONObject data){		
		//POST headers
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Length", Integer.toString(data.toJSONString().getBytes().length));
		
		//Build URL
		String url = server + "/" + index + "/" + type + "/" + id + "/_update";
		
		//Check data for script and upsert to get update or create behavior
		if (!data.containsKey("script") && !data.containsKey("doc_as_upsert")){
			JSONObject dataUpdate = new JSONObject();
			JSON.put(dataUpdate, "doc", data);
			JSON.put(dataUpdate, "doc_as_upsert", new Boolean(true));
			data = dataUpdate;
		}
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpPOST(url, data.toJSONString(), headers);
		//System.out.println(result.toJSONString()); 		//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "updateDocument", tic);
			return 0;
		}
		//error
		else{
			Debugger.println("updateDocument - ElasticSearch - error in '" + index + "/" + type + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "updateDocument" + "-error", tic);
			return 1;
		}
	}
	
	/**
	 * Get document at path "index/type/id".
	 * @param index - index name, e.g. "account"
	 * @param type - type name, e.g. "user"
	 * @param id - id name/number, e.g. user_id
	 * @return JSONObject with document data or error
	 */
	public JSONObject getDocument(String index, String type, String id){		
		//Build URL
		String url = server + "/" + index + "/" + type + "/" + id;
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpGET(url);
		//System.out.println(result.toJSONString()); 		//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "getDocument", tic);
			return result;
		}
		//error
		else{
			Debugger.println("getDocument - ElasticSearch - error in '" + index + "/" + type + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "getDocument" + "-error", tic);
			return result;
		}
	}
	/**
	 * Get document at path "index/type/id" with filtered entries.
	 * @param index - index name, e.g. "account"
	 * @param type - type name, e.g. "user"
	 * @param id - id name/number, e.g. user_id
	 * @param sources - entries in the document you want to retrieve, e.g. "name,address,email", separated by a simple ",". All empty space is removed.
	 * @return JSONObject with document data or null. If sources are missing they are ignored.
	 */
	public JSONObject getDocument(String index, String type, String id, String sources){
		return getDocument(index, type, id + "?_source=" + sources.replaceAll("\\s+", "").trim());
	}
	
	/**
	 * Delete document at "index/type/id".
	 * @param index - index name, e.g. "account"
	 * @param type - type name, e.g. "user"
	 * @param id - id name/number, e.g. user_id
	 * @return error code (0 - no error, 1 - no connection or fail)
	 */
	public int deleteDocument(String index, String type, String id){
		//Build URL
		String url = server + "/" + index + "/" + type + "/" + id;
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpDELETE(url);
		//System.out.println(result.toJSONString()); 		//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "deleteDocument", tic);
			return 0;
		}
		//error
		else{
			Debugger.println("deleteDocument - ElasticSearch - error in '" + index + "/" + type + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "deleteDocument" + "-error", tic);
			return 1;
		}
	}
	/**
	 * Delete anything.
	 * @param path - path to index or index/type or whatever (required)
	 * @return error code (0 - no error, 1 - no connection or fail)
	 */
	public int deleteAny(String path){
		//Build URL
		String url = server + "/" + path;
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpDELETE(url);
		//System.out.println(result.toJSONString()); 		//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "deleteAny", tic);
			return 0;

		//error
		}else{
			Debugger.println("deleteAny - ElasticSearch - error in '" + path + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "deleteAny" + "-error", tic);
			return 1;
		}
	}
	
	//Statics:
	
	/**
	 * Add a mapping to an index. You can use JSON.readJsonFromFile to get "data" form a JSON file.
	 * @return JSON response with error code.
	 */
	public static JSONObject putMapping(String server, String index, JSONObject data){
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Length", Integer.toString(data.toJSONString().getBytes().length));
		
		//Build URL
		String url = server + "/" + index;
		
		long tic = System.currentTimeMillis();
		JSONObject result = Connectors.httpPUT(url, data.toJSONString(), headers);
		//System.out.println(result.toJSONString()); 		//debug
		
		//success?
		if (Connectors.httpSuccess(result)){
			Statistics.addInternalApiHit(API_NAME + ":" + "putMapping", tic);
			return JSON.make("code", 0);
		}
		//error
		else{
			Debugger.println("putMapping - ElasticSearch - error in '" + index + "': " + result.toJSONString(), 1);
			Statistics.addInternalApiHit(API_NAME + ":" + "putMapping" + "-error", tic);
			return JSON.make("code", 1);
		}
	}
	
	/**
	 * Define a custom GET request to Elasticsearch cluster. Examples:<br>
	 * customGET(index + "/" + type, "_count", "")<br>
	 * customGET(index + "/" + type, "_search", URLBuilder.getString("", "?q=", "*", "&size=", "20"))<br>
	 * @param server - ES end-point URL
	 * @param path - path to index or index/type or whatever (required)
	 * @param api - should be an API like "_search" or "_count" etc. but can also be empty or complete custom query
	 * @param urlParameters - parameter string like "?q=user:myid1" (can be empty, but not null). You can use URLBuilder.getString("", "?q=", "user:myid1") to avoid manual URLEncoding. 
	 * @return raw JSON result or JSON with "error"
	 */
	public static JSONObject customGET(String server, String path, String api, String urlParameters){
		//Build URL
		if (!path.endsWith("/") && !path.isEmpty()) { path = path + "/"; }
		try{
			String url = server + "/" + path.trim() + api.trim() + urlParameters.trim();
		
			long tic = System.currentTimeMillis();
			JSONObject result = Connectors.httpGET(url);
			//System.out.println(result.toJSONString()); 		//debug
			
			//success?
			if (Connectors.httpSuccess(result)){
				Statistics.addInternalApiHit(API_NAME + ":" + "customGET", tic);
				return result;

			//error
			}else{
				Debugger.println("customGET - ElasticSearch - error in '" + path + api + "': " + result.toJSONString(), 1);
				Statistics.addInternalApiHit(API_NAME + ":" + "customGET" + "-error", tic);
				return result;
			}
		//error
		}catch (Exception e){
			JSONObject res = new JSONObject();
			JSON.add(res, "error", "request failed! - e: " + e.getMessage());
			JSON.add(res, "code", -1);
			return res;
		}
	}
	
	/**
	 * Define a custom PUT request to Elasticsearch cluster.
	 * @param server - ES end-point URL
	 * @param path - path to index or index/type or whatever (required)
	 * @param data - PUT body
	 * @return raw JSON result or JSON with "error"
	 */
	public static JSONObject customPUT(String server, String path, JSONObject data){	
		try{
			//PUT headers
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("Content-Length", Integer.toString(data.toJSONString().getBytes().length));
			
			//Build URL
			String url = server + "/" + path.trim();
			
			long tic = System.currentTimeMillis();
			JSONObject result = Connectors.httpPUT(url, data.toJSONString(), headers);
			//System.out.println(result.toJSONString()); 		//debug
			
			//success?
			if (Connectors.httpSuccess(result)){
				Statistics.addInternalApiHit(API_NAME + ":" + "customPUT", tic);
				return result;
			
			//error
			}else{
				Debugger.println("customPUT - ElasticSearch - error in '" + path + "': " + result.toJSONString(), 1);
				Statistics.addInternalApiHit(API_NAME + ":" + "customPUT" + "-error", tic);
				return result;
			}
		//error
		}catch (Exception e){
			JSONObject res = new JSONObject();
			JSON.add(res, "error", "request failed! - e: " + e.getMessage());
			JSON.add(res, "code", -1);
			return res;
		}
	}
	
	/**
	 * Delete anything.
	 * @param server - ES end-point URL
	 * @param path - path to index or index/type or whatever (required)
	 * @return raw JSON result or JSON with "error"
	 */
	public static JSONObject customDELETE(String server, String path){
		//Build URL
		if (!path.endsWith("/") && !path.isEmpty()) { path = path + "/"; }
		String url = server + "/" + path;
		try{
			long tic = System.currentTimeMillis();
			JSONObject result = Connectors.httpDELETE(url);
			//System.out.println(result.toJSONString()); 		//debug
			
			//success?
			if (Connectors.httpSuccess(result)){
				Statistics.addInternalApiHit(API_NAME + ":" + "customDELETE", tic);
				return result;
	
			//error
			}else{
				Debugger.println("customDELETE - ElasticSearch - error in '" + path + "': " + result.toJSONString(), 1);
				Statistics.addInternalApiHit(API_NAME + ":" + "customDELETE" + "-error", tic);
				return result;
			}
		//error
		}catch (Exception e){
			JSONObject res = new JSONObject();
			JSON.add(res, "error", "request failed! - e: " + e.getMessage());
			JSON.add(res, "code", -1);
			return res;
		}
	}
	
	//-------- Helpers ---------
	
	/**
	 * Get "hits" of a search query. If the query had no results return empty array.
	 */
	public static JSONArray getHits(JSONObject searchResult){
		JSONArray hits = JSON.getJArray(searchResult, new String[]{"hits", "hits"});
		if (hits == null) hits = new JSONArray();
		return hits;
	}
}
