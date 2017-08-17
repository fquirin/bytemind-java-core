package de.bytemind.core.databases;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.bytemind.core.tools.Connectors;
import de.bytemind.core.tools.JSON;

public class DynamoDbTestSequence {

	//DynamoDB test sequence:
	public static void main(String[] args) {
		
		//Setup
		DynamoDbConfig.setRegion("http://localhost:21311");
		DynamoDbConfig.setAccess("EXAMPLEMDDBBTTRRXXYY");
		DynamoDbConfig.setSecret("ExampleDefghIa1BcDeFghI2JkLm");
		
		//Test table
		String tableName = "users";
		String primaryKey = "Guuid";
		String secondaryIndex = "Email";
		
		//Test item
		String guuid = "uid1000";
		String email = "admin@example.com";
		Map<String, String> name = new HashMap<>();
		name.put("first", "Senior");
		name.put("last", "Boss");
		
		//Clean
		System.out.println("---Clean table (try)---");
		JSONObject res = DynamoDB.deleteTable(tableName);
		JSON.printJSONpretty(res);		//if (!Connectors.httpSuccess(res)){return;}
		
		//List
		System.out.println("\n---List---");
		res = DynamoDB.listTables();
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Create table
		System.out.println("\n---Create table---");
		res = DynamoDB.createSimpleTable(tableName, primaryKey, secondaryIndex);
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//List
		System.out.println("\n---List---");
		res = DynamoDB.listTables();
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Create item
		System.out.println("\n---Create Item---");
		int code = DynamoDB.writeAny(tableName, primaryKey, guuid, new String[]{"Email", "name"}, new Object[]{email, name});
		if (code != 0){ System.out.println("ERROR, code: " + code); return; } else System.out.println("SUCCESS");
		
		//Get item - pK
		System.out.println("\n---Get Item by primaryKey---");
		res = DynamoDB.getItem(tableName, primaryKey, guuid, "Email", "name.first", "name.last");
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Overwrite item
		System.out.println("\n---Overwrite Item attribute---");
		code = DynamoDB.writeAny(tableName, primaryKey, guuid, new String[]{"name.last"}, new Object[]{""});
		if (code != 0){ System.out.println("ERROR, code: " + code); return; } else System.out.println("SUCCESS");
		
		//Add items
		System.out.println("\n---Add Item attribute---");
		code = DynamoDB.writeAny(tableName, primaryKey, guuid, new String[]{"name.nick", "Phone"}, new Object[]{"Testy", "55555"});
		if (code != 0){ System.out.println("ERROR, code: " + code); return; } else System.out.println("SUCCESS");
		
		//Get item - sI
		System.out.println("\n---Get Item by secondaryIndex---");
		res = DynamoDB.queryIndex(tableName, secondaryIndex, email, "Email", "name", "Phone");
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Delete item
		System.out.println("\n---Delete Item---");
		code = DynamoDB.deleteItem(tableName, primaryKey, guuid);
		if (code != 0){ System.out.println("ERROR, code: " + code); return; } else System.out.println("SUCCESS");
		
		//Get item - pK
		System.out.println("\n---Get Item by primaryKey---");
		res = DynamoDB.getItem(tableName, primaryKey, guuid, "Email");
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		//Clean
		System.out.println("\n---Clean table---");
		res = DynamoDB.deleteTable(tableName);
		JSON.printJSONpretty(res);		if (!Connectors.httpSuccess(res)){return;}
		
		System.out.println("\n--- ALL DONE :-) ---");
	}

}
