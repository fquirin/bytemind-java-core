package de.bytemind.core.databases;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Interface for key-value based databases e.g. for fast user authentication etc.
 * 
 * @author Florian Quirin
 *
 */
public interface KeyValueDatabase {
	
	/**
	 * Test if database connection works.
	 */
	public boolean testConnection();
	
	public JSONObject getValues(String table, String primeIndexName, String primeIndexValue, String... keys);
	
	public JSONObject getValuesBySecondayIndex(String table, String secIndexName, String secIndexValue, String... keys);
	
	
	public JSONObject setValue(String table, String primeIndexName, String primeIndexValue, String key, String value);
	
	public JSONObject setValue(String table, String primeIndexName, String primeIndexValue, String key, long value);
	
	public JSONObject setValue(String table, String primeIndexName, String primeIndexValue, String key, Map<String, Object> value);
	
	public JSONObject setValue(String table, String primeIndexName, String primeIndexValue, String key, List<Object> value);
	
	
	public JSONObject deleteKey(String table, String primeIndexName, String primeIndexValue, String key);
	
	public JSONObject deleteIndex(String table, String primeIndexName, String primeIndexValue);

}
