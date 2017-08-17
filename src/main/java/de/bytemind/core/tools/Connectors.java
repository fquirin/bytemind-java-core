package de.bytemind.core.tools;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Connectors to simplify calling other APIs like HTTP GET etc.
 *  
 * @author Florian Quirin
 *
 */
public class Connectors {
	
	private static final String USER_AGENT = "Mozilla/5.0";
	public static final String HTTP_REST_SUCCESS = "HTTP_REST_SUCCESS";

	/**
	 * Sends a GET and parses the reply as JSON. Other than {@code httpGET_JSON},
	 * this leaves the reply unmodified, i.e. it won't add {@code HTTP_REST_SUCCESS}.
	 * Throws a RuntimeException on fail.
	 */
	public static JSONObject simpleJsonGet(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			//con.setRequestProperty("content-type", "text/html");
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK){
				try (InputStream stream = con.getInputStream();
						InputStreamReader isr = new InputStreamReader(stream, Charsets.UTF_8)) {
					String content = CharStreams.toString(isr);
					return JSON.parseStringOrFail(content);
				}
			} else {
				throw new RuntimeException("Could not get '" + url + "': response code " + responseCode);
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not get '" + url + "', error: " + e.getMessage(), e);
		}
	}
	/**
	 * Sends a GET and returns result as string. 
	 * Throws a RuntimeException on fail.
	 */
	public static String simpleHtmlGet(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("content-type", "text/html");
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK){
				try (InputStream stream = con.getInputStream();
						 InputStreamReader isr = new InputStreamReader(stream, Charsets.UTF_8)) {
					String content = CharStreams.toString(isr);
					return content;
				}
			} else {
				throw new RuntimeException(DateTime.getLogDate() + " ERROR - Could not get '" + url + "': response code " + responseCode);
			}
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " ERROR - Could not get '" + url + "', error: " + e.getMessage(), e);
		}
	}
	
	/**
	 * HTTP GET method for JSON string. Check with {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including all parameters
	 * @return JSONObject response of URL call
	 */
	public static JSONObject httpGET(String url) {
		return httpGET(url, new String[0]);
	}
	/**
	 * HTTP GET method for JSON string. Check with {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters
	 * @param params - additional parameters added to URL (use e.g. "?q=search_term" or "&type=json" etc.)
	 * @return
	 */
	public static JSONObject httpGET(String url, String[] params) {
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try{
			for (String s : params){
				url = url + s;
			}
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
	
			responseCode = con.getResponseCode();
			//System.out.println("GET Response Code : " + responseCode);		//debug
	
			//success?
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				/*
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				//result
				//System.out.println(response.toString());						//debug
				String res = response.toString();
				JSONObject result = build(res, success_str);
				return result;
				*/
				InputStream stream = con.getInputStream();
				InputStreamReader isr = new InputStreamReader(stream, Charsets.UTF_8);
				String content = CharStreams.toString(isr);
				JSONObject result = build(content, success_str);
				return result;
		
			}else{
				//result
				//System.out.println("GET request did not work");				//debug
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, new Boolean(false));
				JSON.add(json, "code", new Integer(responseCode));
				return json;
			}
			
		}catch (Exception e){
			//result
			//System.out.println("GET request did not work");					//debug
			JSONObject json = new JSONObject();
			JSON.add(json, success_str, new Boolean(false));
			JSON.add(json, "error", e.toString());
			JSON.add(json, "code", new Integer(responseCode));
			return json;
		}
	}
	
	//--------------------------POST--------------------------------
	
	/**
	 * Make a HTTP POST request to targetUrl with x-www-form-urlencoded parameters. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param urlParameters - parameters for x-www-form-urlencoded content-type, e.g. "a=1&b=2&c=3..." 
	 * @return server answer as JSONObject
	 */
	public static JSONObject httpPOST(String targetURL, String urlParameters) {
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Content-Length", Integer.toString(urlParameters.getBytes().length));
		headers.put("Content-Language", "en-US");
			
		return httpPOST(targetURL, urlParameters, headers);
	}
	/**
	 * Make a HTTP POST request to targetUrl with custom headers. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param data - data in chosen content-type, e.g. url parameter style or JSON string
	 * @param headers - HashMap with request properties (keys) and values.
	 * @return JSONObject with response
	 */
	public static JSONObject httpPOST(String targetURL, String data, HashMap<String, String> headers) {
		URL url;
		HttpURLConnection connection = null;
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try {
			
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			//System.out.println("---headers---");
			for (Map.Entry<String, String> entry : headers.entrySet())
			{
				connection.setRequestProperty(entry.getKey(), entry.getValue());
				//System.out.println(entry.getKey() +": "+ entry.getValue());
			}			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(data.getBytes(StandardCharsets.UTF_8));
			//wr.writeBytes(data);
			wr.flush();
			wr.close();
			
			responseCode = connection.getResponseCode();
			//System.out.println("POST Response Code : " + responseCode);		//debug

			//Get Response
			InputStream is;
			boolean success = false;
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				success = true;
				is = connection.getInputStream();
			}else{
				is = connection.getErrorStream();
			}
			/*
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				//response.append('\r');	//line break messes it all up
			}
			rd.close();
			String res = response.toString();
			*/
			InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8);
			String res = CharStreams.toString(isr);
			
			if (success){
				JSONObject result = build(res, success_str);
				return result;
			}else{
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, new Boolean(false));
				JSON.add(json, "code", new Integer(responseCode));
				JSON.add(json, "error", res);
				return json;
			}

	    }catch (Exception e) {
	    	JSONObject json = new JSONObject();
			JSON.add(json, success_str, new Boolean(false));
			JSON.add(json, "code", new Integer(responseCode));
			JSON.add(json, "error", e.toString());
			return json;

	    }finally {
	    	if(connection != null) {
	    		connection.disconnect(); 
	    	}
	    }
	}
	
	//----------------------PUT-------------------------
	
	/**
	 * Make a HTTP PUT request to targetUrl with custom headers. Check {@code httpSuccess(...)} for status.
	 * @param targetURL - URL of service
	 * @param data - data in chosen content-type, e.g. url parameter style or JSON string
	 * @param headers - HashMap with request properties (keys) and values.
	 * @return JSONObject with response
	 */
	public static JSONObject httpPUT(String targetURL, String data, HashMap<String, String> headers) {
		URL url;
		HttpURLConnection connection = null;
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try {
			
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("PUT");
			//System.out.println("---headers---");
			for (Map.Entry<String, String> entry : headers.entrySet())
			{
				connection.setRequestProperty(entry.getKey(), entry.getValue());
				//System.out.println(entry.getKey() +": "+ entry.getValue());
			}			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(data.getBytes(StandardCharsets.UTF_8));
			//wr.writeBytes(data);
			wr.flush();
			wr.close();
			
			responseCode = connection.getResponseCode();
			//System.out.println("POST Response Code : " + responseCode);		//debug

			//Get Response
			InputStream is;
			boolean success = false;
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				success = true;
				is = connection.getInputStream();
			}else{
				is = connection.getErrorStream();
			}
			/*
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				//response.append('\r');	//line break messes it all up
			}
			rd.close();
			//String res = response.toString();
			*/
			InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8);
			String res = CharStreams.toString(isr);
			
			if (success){
				JSONObject result = build(res, success_str);
				return result;
				
			}else{
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, new Boolean(false));
				JSON.add(json, "code", new Integer(responseCode));
				JSON.add(json, "error", res);
				return json;
			}

	    }catch (Exception e) {
	    	JSONObject json = new JSONObject();
			JSON.add(json, success_str, new Boolean(false));
			JSON.add(json, "code", new Integer(responseCode));
			JSON.add(json, "error", e.toString());
			return json;

	    }finally {
	    	if(connection != null) {
	    		connection.disconnect(); 
	    	}
	    }
	}
	
	//-------------DELETE--------------
	
	/**
	 * HTTP GET method for JSON string. Use {@code httpSuccess(...)} for status.
	 * @param url - URL address to call including none or only some parameters
	 * @param params - additional parameters added to URL (use e.g. "?q=search_term" or "&type=json" etc.)
	 * @return
	 */
	public static JSONObject httpDELETE(String url) {
		int responseCode = -1;
		String success_str = HTTP_REST_SUCCESS;
		try{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			con.setRequestMethod("DELETE");
			con.setRequestProperty("User-Agent", USER_AGENT);
	
			responseCode = con.getResponseCode();
			//System.out.println("GET Response Code : " + responseCode);		//debug
	
			//success?
			if (responseCode >= 200 && responseCode < 300){		//(responseCode == HttpURLConnection.HTTP_OK){
				/*
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
	
				//result
				//System.out.println(response.toString());						//debug
				String res = response.toString();
				*/
				InputStreamReader isr = new InputStreamReader(con.getInputStream(), Charsets.UTF_8);
				String res = CharStreams.toString(isr);
				JSONObject result = build(res, success_str);
				return result;
		
			}else{
				//result
				//System.out.println("GET request did not work");				//debug
				JSONObject json = new JSONObject();
				JSON.add(json, success_str, new Boolean(false));
				JSON.add(json, "code", new Integer(responseCode));
				return json;
			}
			
		}catch (Exception e){
			//result
			//System.out.println("GET request did not work");					//debug
			JSONObject json = new JSONObject();
			JSON.add(json, success_str, new Boolean(false));
			JSON.add(json, "error", e.toString());
			JSON.add(json, "code", new Integer(responseCode));
			return json;
		}
	}
	
	//-------------- COMMON ------------------
	
	/**
	 * Convenience method to check if the HTTP REST call was successful.<br>
	 * The HTTP REST methods used here add a helper field to the result to track the state of the call.
	 * With this method you can check if this field exists and says "true".<br>
	 * Note: If the return value is "false" it can also mean that the HTTP return code of the request was bigger 300 or higher
	 */
	public static boolean httpSuccess(JSONObject response){
		if (response == null){
			return false;
		}
		Object restO = response.get(HTTP_REST_SUCCESS);
		boolean rest = (restO == null)? false : (boolean) restO;
		return rest;
	}
	
	/**
	 * Get a readable string of the submitted error if the request was not successful.
	 */
	public static String httpError(JSONObject response){
		return ("code: " + response.get("code") + ", error: " + response.get("error"));
	}
	
	/**
	 * Build result depending on type of reply (JSON string, JSON array, simple string).
	 * Adds the "success" tag as well.
	 * @param res - result received from HTTP connection as string
	 * @param successTag - field to add indicating the success
	 */
	private static JSONObject build(String res, String successTag){
		//System.out.println(res);						//debug
		res = res.charAt(0) == '\uFEFF' ? res.substring(1) : res;
		res = res.replaceAll("^(\\r+|\\n+|\\t+)", "").trim();
		//debug:
		/*
		System.out.println(res.substring(0, 1));
		for (char c : res.toCharArray()) {
		    System.out.printf("U+%04x ", (int) c);
		    break;
		}
		*/
		JSONObject result;
		try{
			if (res.startsWith("{")){
				//parse JSONObject
				JSONParser parser = new JSONParser();
				result = (JSONObject) parser.parse(res);
				JSON.add(result, successTag, new Boolean(true));
			}else if (res.startsWith("[{")){
				//parse JSONArray
				JSONParser parser = new JSONParser();
				JSONArray arr = (JSONArray) parser.parse(res);
				result = new JSONObject();
				JSON.add(result, "JSONARRAY", arr);
				JSON.add(result, successTag, new Boolean(true));
			}else{
				//save String only
				result = new JSONObject();
				JSON.add(result, "STRING", res);
				JSON.add(result, successTag, new Boolean(true));
			}
			//System.out.println(result.toString());						//debug
			return result;
		}catch (ParseException e){
			System.err.println(DateTime.getLogDate() + " ERROR - Connectors.java / build() - Failed to parse JSON string: " + res);
			result = new JSONObject();
			JSON.add(result, successTag, new Boolean(false));
			JSON.add(result, "error", "result could not be parsed");
			JSON.add(result, "code", "500");
			return result;
		}
	}

}
