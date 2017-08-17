package de.bytemind.core.databases;

/**
 * Configuration for DynamoDB. This is usually set during server start (e.g. via a loadConfigFile method).
 * 
 * @author Florian Quirin
 *
 */
public class DynamoDbConfig {

	public static String service = "dynamodb";
	public static String region = "eu-central-1";	//"http://localhost:8000";
	
	public static String host = service + "." + region + ".amazonaws.com";
	public static String endpoint = "https://" + host;
	
	static String amazon_dynamoDB_secret = "";
	static String amazon_dynamoDB_access = "";
	
	public static String getService(){
		return service;
	}
	
	public static String getRegion(){
		return region;
	}
	public static void setRegion(String newRegion){
		region = newRegion;
	}
	
	public static String getHost(){
		String region = getRegion();
		if (region.startsWith("http")){
			host = region;
			return region;
		}else{
			host = service + "." + region + ".amazonaws.com";
			return host;
		}
	}
	
	public static String getEndpoint(){
		String host = getHost();
		if (host.startsWith("http")){
			endpoint = host;
			return endpoint;
		}else{
			endpoint = "https://" + host;
			return endpoint;
		}
	}
	
	/**
	 * Get DynamoDB credentials access. Can be arbitrary for local instance.
	 */
	static String getAccess(){
		return amazon_dynamoDB_access;
	}
	/**
	 * Set DynamoDB credentials access. Can be arbitrary for local instance. Should be done in server configuration file.
	 */
	public static void setAccess(String access){
		amazon_dynamoDB_access = access;
	}
	/**
	 * Get DynamoDB credentials secret. Can be arbitrary for local instance.
	 */
	static String getSecret(){
		return amazon_dynamoDB_secret;
	}
	/**
	 * Set DynamoDB credentials secret. Can be arbitrary for local instance. Should be done in server configuration file.
	 */
	public static void setSecret(String secret){
		amazon_dynamoDB_secret = secret;
	}

}
