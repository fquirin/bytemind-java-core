package de.bytemind.core.configuration;

import org.json.simple.JSONObject;

import de.bytemind.core.client.ClientDefaults;
import de.bytemind.core.databases.DynamoDbConfig;
import de.bytemind.core.databases.ElasticSearchConfig;
import de.bytemind.core.tools.JSON;
import de.bytemind.core.users.IdHandler;

/**
 * Core provides a lot of convenience methods and classes across different ByteMind clients and servers and all of them can 
 * have different configurations. It is recommended to setup the core library in every project at the beginning depending on what you use.
 * 
 * @author Florian Quirin
 *
 */
public class CoreSetup {
	
	public static JSONObject settings;
	
	public static void load(JSONObject newSettings){
		settings = newSettings;
		
		if (settings.containsKey("authentication_module"))	ClientDefaults.authentication_module = JSON.getString(settings, "authentication_module");
		if (settings.containsKey("auth_endpoint_url"))		ClientDefaults.auth_endpoint_url = JSON.getString(settings, "auth_endpoint_url");
		
		if (settings.containsKey("db_dynamo_region"))		DynamoDbConfig.setRegion(JSON.getString(settings, "db_dynamo_region"));
		if (settings.containsKey("amazon_dynamoDB_access"))	DynamoDbConfig.setAccess(JSON.getString(settings, "amazon_dynamoDB_access"));
		if (settings.containsKey("amazon_dynamoDB_secret"))	DynamoDbConfig.setSecret(JSON.getString(settings, "amazon_dynamoDB_secret"));
		
		if (settings.containsKey("db_elastic_endpoint"))	ElasticSearchConfig.setEndpoint(JSON.getString(settings, "db_elastic_endpoint"));
		
		if (settings.containsKey("user_id_prefix"))			IdHandler.user_id_prefix = JSON.getString(settings, "user_id_prefix");
	}

}
