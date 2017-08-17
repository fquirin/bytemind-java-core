package de.bytemind.core.tools;

import java.net.URLEncoder;

/**
 * A class to help you quickly build URLs.
 * 
 * @author Florian Quirin
 *
 */
public class URLBuilder {
	
	/**
	 * Build an URL by using tuples of 2 where the first is the parameter and the second is always the value that gets URL encoded.
	 * There must be at least one tuple given and obviously the length of keyValues must be even. "?", "&" and "=" must be added by hand.
	 * @param baseURL - URL to start with, e.g. http://www.google.com
	 * @param keyValues - tuples of parameter-value, e.g. "?q=" and "shoes"
	 * @return
	 */
	public static String getString(String baseURL, String... keyValues){
		try {
			String fullURL = baseURL;
			int i = 0;
			while (i < keyValues.length){
				fullURL += keyValues[i] + URLEncoder.encode(keyValues[i+1], "UTF-8");
				i = i + 2;
			}
			return fullURL;
			
		} catch (Exception e) {
			throw new RuntimeException(DateTime.getLogDate() + " WARNING - URLBuilder.getString() - Failed to build URL: " + keyValues, e);
		}
	}

}
