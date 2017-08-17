package de.bytemind.core.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Converters mostly from "something" to JSON and other helper methods like type conversions and even a random generator.
 * 
 * @author Florian Quirin
 *
 */
public class Converters {
	
	/**
	 * Take a sentence (or multiple) and remove all special characters so you can use it as a database ID. 
	 * @param sentence - a sentence or multiple separated by "."
	 * @return clean string that can be used as ID. Note: you have to manually check if its empty now!
	 */
	public static String makeIDfromSentence(String sentence){
		String id = sentence.replaceAll("\\.\\s", "__");
		id = id
			.replaceAll("\\s+", "_")
			.replaceAll("İ", "i")
			.toLowerCase()
			.replaceAll("ö", "oe").replaceAll("ä", "ae").replaceAll("ü", "ue")
			.replaceAll("\\W", "")
			.trim();
		//TODO: what if it is empty now?
		
		return id;
	}
	
	/**
	 * Remove all non-alphabetic and non-digit characters
	 */
	public static String cleanString(String text){
		return (text.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", "").trim());
	}
	
	/**
	 * Removes some prominent HTML code from an answer like &lt;div&gt;, &lt;a&gt;, &lt;span&gt; etc.. <br> 
	 * Note: it's not even close to being complete, I'd prefer to let the specific client do this when possible.
	 * @param input - string to relieve from some HTML code
	 * @return
	 */
	public static String removeSomeHTML(String input){
		String out = input;
		out = out.replaceAll("(<div .*?>|<a .*?>|<p .*?>|<span .*?>|<img .*?>)", "");
		out = out.replaceAll("(<div>|<a>|<img>|<p>|<span>)", " ");
		out = out.replaceAll("(</div>|</a>|</img>|<br>|</p>|</span>)", " ");
		out = out.replaceAll("( )+", " ");
		return out;
	}

	/**
	 * Encode URL with UTF8.
	 * @param url - URL as conventional string 
	 * @return encoded URL or empty string if encoding fails
	 */
	public static String encodeUrl(String url){
		try {
			return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * Take an object where you are SURE! it is a valid double, either as number or string, round it to 1% precision 
	 * and convert it back to string without trailing zeros.
	 * @param val - value to round, is object but MUST BE a valid double
	 * @param round_to_int - ignore precision and round to integer?
	 * @return rounded double as string or empty
	 */
	public static String smartRound(Object val, boolean round_to_int){
		double v = obj2double(val, Double.NEGATIVE_INFINITY);
		if (v == Double.NEGATIVE_INFINITY){
			System.err.println(DateTime.getLogDate() + " ERROR - Converters.java smartRound(..) FAILED! with: " + val.toString());
			return "";
		}else{
			try {
				String r;
				if (v >= 100 || round_to_int){
					r = Double.toString(Math.round(v));
				}else if (v >= 10){
					r = Double.toString(Math.round(v * 10.0d)/10.0d);
				}else if (v >= 1){
					r = Double.toString(Math.round(v * 100.0d)/100.0d);
				}else{
					//TODO: fix this - it should do 0.071124754000 -> 0.0711
					r = Double.toString(v);
				}
				r = r.replaceFirst("(\\.|,)(.*)(0+$)", "$1$2").trim().replaceFirst("(\\.|,)$", "").trim();
				return r;
			}catch (Exception e){
				return "";
			}
		}
	}
	
	/**
	 * Convert a string to a long. If it fails the long will be -1. Best used for System.time checks.
	 * @param in - string in long-format to convert
	 * @return long value or -1. If -1 is important to you DON'T use this method
	 */
	public static long str2long(String in){
		try {
			return Long.parseLong(in);
		} catch (Exception e){
			return -1;
		}
	}
	/**
	 * Convert an unknown object that holds a double (real double or string double) to double.
	 * @param in - string to convert, must be in double format
	 * @param defaultVal - value given when parsing fails. Choose wisely :-)
	 * @return double or defaultVal
	 */
	public static double obj2double(Object in, double defaultVal){
		try {
			return Double.parseDouble((String.valueOf(in)));
		} catch (Exception e){
			return defaultVal;
		}
	}
	/**
	 * Convert an unknown object that holds a double (number or string) to long.
	 * @param in - string to convert, must be in double format (e.g. 1, 1.0, etc.)
	 * @param defaultVal - value given when parsing fails. Choose wisely :-)
	 * @return long or defaultVal
	 */
	public static long obj2long(Object in, long defaultVal){
		try {
			return (long) Double.parseDouble((String.valueOf(in)));
		} catch (Exception e){
			return defaultVal;
		}
	}
	/**
	 * Convert an unknown object that holds an integer/double (number or string) to an integer.
	 * Removes all non-numbers (except .,+-) in the string and casts double to int!
	 * @param in - string to convert, must be in double format (e.g. 1, 1.0, etc.)
	 * @param defaultVal - value given when parsing fails. Choose wisely :-)
	 * @return int value or defaultVal
	 */
	public static int obj2int(Object in, int defaultVal){
		try {
			return (int) Double.parseDouble((String.valueOf(in).replaceAll("[^\\d\\.,\\-\\+]", "")));
		} catch (Exception e){
			return defaultVal;
		}
	}
	
	/**
	 * Convert from string to JSON Object
	 * @param s - String to parse
	 * @return JSONObject or null when parsing fails
	 */
	public static JSONObject str2Json(String s){
		JSONParser parser = new JSONParser();
		JSONObject result;
		try {
			result = (JSONObject) parser.parse(s);
			return result;
		} catch (ParseException e) {
			return null;
			//e.printStackTrace();
		}
	}
	
	/**
	 * Take a number of objects and make an ArrayList out of it.
	 */
	public static ArrayList<Object> objects2ArrayList(Object...objects){
		ArrayList<Object> al = new ArrayList<>();
		for (Object o : objects){
			al.add(o);
		}
		return al;
	}
	
	/**
	 * Convert a default (SEPIA) data string to a JSON object. This data type is used sometimes when JSON simply did not work well (actually thats not very often). 
	 * @param dataString - standard (for this framework) formatted data string, e.g. &#60city&#62Berlin&#60country&#62Germany
	 * @return JSONObject with keys mapped or empty JSONObject
	 */
	public static JSONObject dataString2Json(String dataString){
		String[] s1 = dataString.split("<");
		String[] s2;
		if (s1.length > 1){
			JSONObject js = new JSONObject();
			for (String s : s1){
				s2 = s.split(">",2);
				if (s2.length == 2){
					JSON.add(js, s2[0], s2[1]);
				}
			}
			return js;
		
		}else{
			return new JSONObject();
		}
	}

	/**
	 * Converts a HashMap&lt;String, ?&gt; to a JSONObject
	 * 
	 * @param m - hashMap&lt;String, ?&gt;
	 * @return
	 */
	public static JSONObject map2Json(Map<String, ?> m){
		JSONObject params = new JSONObject();
		for (Map.Entry<String, ?> entry : m.entrySet()) {
			String p = entry.getKey();
			Object v = entry.getValue();
			JSON.put(params, p, v);
		}
		return params;
	}
	
	/**
	 * Converts a HashMap&lt;String,String&gt; to a single string separated by ";;"
	 * 
	 * @param m - hashMap&lt;String, String&gt;
	 * @return
	 */
	public static String map2Str(Map<String, String> m){
		String result="";
		for (Map.Entry<String, String> entry : m.entrySet()) {
			String p = entry.getKey();
			String v = entry.getValue();
			result += p + "=" + v + ";;";
		}
		return result.trim();
	}
	
	/**
	 * Convert JSONObject to HashMap&lt;String,String&gt; by transferring all TOP-LEVEL key-value pairs. All values should be at least convertible to "String".
	 * Nested values are converted to strings.
	 * @param jsonObject - simple JSON object
	 * @return HashMap&lt;String,String&gt; (can be empty)
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> json2HashMap_SS(JSONObject jsonObject) {
		Map<String,String> params = new HashMap<>();
		if (jsonObject == null) {
			return params;
		} else {
			for (Object entry : jsonObject.entrySet()) {
				Map.Entry<String, Object> entryObj = (Map.Entry<String, Object>) entry;
				params.put(entryObj.getKey(), entryObj.getValue().toString());
			}
		}
		return params;
	}
	/**
	 * Convert JSONObject to HashMap&lt;String,Object&gt; by transferring all TOP-LEVEL key-value pairs.
	 * Nested JSONObjects remain what they are.
	 * @param jsonObject - simple JSON object
	 * @return HashMap&lt;String,String&gt; (can be empty)
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> json2HashMap(JSONObject jsonObject) {
		Map<String, Object> params = new HashMap<>();
		if (jsonObject == null) {
			return params;
		}else{
			for (Object entry : jsonObject.entrySet()) {
				Map.Entry<String, Object> entryObj = (Map.Entry<String, Object>) entry;
				params.put(entryObj.getKey(), entryObj.getValue());
			}
		}
		return params;
	}
	
	/**
	 * Take a List and make a JSONArray out of it.
	 * @return JSONArray
	 */
	public static JSONArray list2JsonArray(List<?> list){
		JSONArray array = new JSONArray();
		for (Object o : list){
			JSON.add(array, o);
		}
		return array;
	}
	
	/**
	 * Convert JSONArray to ArrayList with object.
	 * @return List with objects or empty
	 */
	public static List<Object> jsonArray2ArrayList(JSONArray ja){
		List<Object> l = new ArrayList<>();
		for (Object o : ja){
			l.add(o);
		}
		return l;
	}
	
	/**
	 * Makes an unchecked (cause you can't check it, can you?) cast from Object to HashMap&#60String, Object&#62.
	 * @param input - object that is supposed to be the expected HashMap 
	 * @return HashMap or null
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> object2HashMap_SO(Object input){
		try {
			HashMap<String, Object> output = (HashMap<String, Object>) input;
			return output;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Makes an unchecked (cause you can't check it, can you?) cast from Object to ArrayList&#60Object&#62.
	 * @param input - object that is supposed to be the expected ArrayList 
	 * @return ArrayList or null
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> object2ArrayList_O(Object input){
		try {
			ArrayList<Object> output = (ArrayList<Object>) input;
			return output;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Makes an unchecked (cause you can't check it, can you?) cast from Object to ArrayList&#60String&#62.
	 * @param input - object that is supposed to be the expected ArrayList 
	 * @return ArrayList or null
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> object2ArrayList_S(Object input){
		try {
			ArrayList<String> output = (ArrayList<String>) input;
			return output;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
