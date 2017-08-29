package de.bytemind.core.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONObject;

/**
 * Class that hold different comparators and reverse search methods like get key to value etc..
 * 
 * @author Florian Quirin
 *
 */
public class Comparators {
	
	/**
	 * Sort map by value.
	 * @param map - unsorted map
	 * @return new sorted LinkedHashMap
	 */
	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortMapByValue(Map<K, V> map, boolean reverseOrder){
		Stream<Entry<K, V>> stream;
		if (!reverseOrder){
			stream = map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue());
		}else{
			stream = map.entrySet()
		           .stream()
		           .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()));
		}
		return stream.collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	/**
	 * A comparator that takes one or two keys in the JSONObject and compares their long values. Secondary will be used when primary keys are
	 * identical 
	 */
	static public class JsonLongValues implements Comparator<Object>{
		
		String primaryKey = "";
		String secondaryKey = "";
		
		/**
		 * Define one or two keys inside the JSON objects that are long objects and will be used to sort the array.
		 * If you don't need the second key set it as empty.
		 */
		public JsonLongValues(String primaryKey, String secondaryKey){
			this.primaryKey = primaryKey;
			this.secondaryKey = secondaryKey;
		}

		@Override
		public int compare(Object o1, Object o2) {
			JSONObject jo1 = (JSONObject) o1;
			JSONObject jo2 = (JSONObject) o2;
			long lp1 = (long) jo1.get(primaryKey);
			long lp2 = (long) jo2.get(primaryKey);
			int primaryComp = Long.compare(lp1, lp2);
			if (primaryComp == 0 && !secondaryKey.isEmpty()){
				long ls1 = (long) jo1.get(secondaryKey);
				long ls2 = (long) jo2.get(secondaryKey);
				return Long.compare(ls1, ls2);
			}else{
				return primaryComp;
			}
		}
	}
	
	/**
	 * A comparator that takes one or two keys in the JSONObject and compares their string values. Secondary will be used when primary keys are
	 * identical 
	 */
	static public class JsonStringValues implements Comparator<Object>{
		
		String primaryKey = "";
		String secondaryKey = "";
		
		/**
		 * Define one or two keys inside the JSON objects that are string objects and will be used to sort the array.
		 * If you don't need the second key set it as empty.
		 */
		public JsonStringValues(String primaryKey, String secondaryKey){
			this.primaryKey = primaryKey;
			this.secondaryKey = secondaryKey;
		}

		@Override
		public int compare(Object o1, Object o2) {
			JSONObject jo1 = (JSONObject) o1;
			JSONObject jo2 = (JSONObject) o2;
			String lp1 = (String) jo1.get(primaryKey);
			String lp2 = (String) jo2.get(primaryKey);
			int primaryComp = lp1.compareTo(lp2);
			if (primaryComp == 0 && !secondaryKey.isEmpty()){
				String ls1 = (String) jo1.get(secondaryKey);
				String ls2 = (String) jo2.get(secondaryKey);
				return ls1.compareTo(ls2);
			}else{
				return primaryComp;
			}
		}
	}
	
	/**
	 * Check if a 'test' value is part of an enum. 
	 * @param enumValues - enum.values
	 * @param test - test string
	 * @return true/false
	 */
	public static <T extends Enum<T>> boolean enumContains(T[] enumValues, String test){
	    for (T ev : enumValues) {
	        if (ev.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Get the first key of a map whose value matches a given test-value. Note: if the value itself is a complex object you need to rewrite this method. 
	 * @param val - test value
	 * @param map - map to search
	 * @return key or null
	 */
	public static <K, V> Optional<K> getKeyForVal(final V val, final Map<K, V> map) {
		try{
			return map.entrySet().stream()
					.filter(e -> e.getValue().equals(val))
			        .map(Map.Entry::getKey)
			        .findFirst();
		}catch(Exception e){
			return null;
		}
	}

}
