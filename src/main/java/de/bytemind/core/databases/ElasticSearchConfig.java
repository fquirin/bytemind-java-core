package de.bytemind.core.databases;

/**
 * Set some common configuration variables for ElasticSearch. This is usually set during server start (e.g. via a loadConfigFile method).
 * 
 * @author Florian Quirin
 *
 */
public class ElasticSearchConfig {
	
	//Cluster endpoint
	public static String endpoint = "http://localhost:8011";
	
	public static String getEndpoint(){
		return endpoint;
	}
	public static void setEndpoint(String newEndpoint){
		endpoint = newEndpoint;
	}
	
}
