package de.bytemind.core.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Capture all sorts of statistics about the server.
 * 
 * @author Florian Quirin
 *
 */
public class Statistics {
	
	private static final long internalApiErrorThreshold = 2000;
	private static final long externalApiErrorThreshold = 3000;
	
	private static HashMap<String, AtomicInteger> externalApiHits = new HashMap<>();
	private static HashMap<String, AtomicLong> externalApiTime = new HashMap<>();
	private static HashMap<String, AtomicInteger> externalApiErrorHits = new HashMap<>();
	private static HashMap<String, AtomicLong> externalApiErrorTime = new HashMap<>();
	
	private static HashMap<String, AtomicInteger> internalApiHits = new HashMap<>();
	private static HashMap<String, AtomicLong> internalApiTime = new HashMap<>();
	private static HashMap<String, AtomicInteger> internalApiErrorHits = new HashMap<>();
	private static HashMap<String, AtomicLong> internalApiErrorTime = new HashMap<>();
	
	//get info
	public static String getInfoAsString(){
		String msg = "Internal APIs:\n"; 
		for (Map.Entry<String, AtomicInteger> entry : internalApiHits.entrySet()) {
			int hit = entry.getValue().get();
			String name = entry.getKey();
			msg += ("- " + name + ": " + hit + " hits\n");
			if (internalApiTime.containsKey(name)){
				msg += ("- " + name + ": " + ((double)internalApiTime.get(name).get())/((double)hit) + " ms per call\n");
			}
		}
		msg += "\n";
		msg += "Internal APIs (possible) errors:\n";
		for (Map.Entry<String, AtomicInteger> entry : internalApiErrorHits.entrySet()) {
			int hit = entry.getValue().get();
			String name = entry.getKey();
			msg += ("- " + name + ": " + hit + " hits\n");
			if (internalApiErrorTime.containsKey(name)){
				msg += ("- " + name + ": " + ((double)internalApiErrorTime.get(name).get())/((double)hit) + " ms per call\n");
			}
		}
		msg += "\n";
		msg += "External APIs:\n";
		for (Map.Entry<String, AtomicInteger> entry : externalApiHits.entrySet()) {
			int hit = entry.getValue().get();
			String name = entry.getKey();
			msg += ("- " + name + ": " + hit + " hits\n");
			if (externalApiTime.containsKey(name)){
				msg += ("- " + name + ": " + ((double)externalApiTime.get(name).get())/((double)hit) + " ms per call\n");
			}
		}
		msg += "\n";
		msg += "External APIs (possible) errors:\n";
		for (Map.Entry<String, AtomicInteger> entry : externalApiErrorHits.entrySet()) {
			int hit = entry.getValue().get();
			String name = entry.getKey();
			msg += ("- " + name + ": " + hit + " hits\n");
			if (externalApiErrorTime.containsKey(name)){
				msg += ("- " + name + ": " + ((double)externalApiErrorTime.get(name).get())/((double)hit) + " ms per call\n");
			}
		}
		msg += "\n";
		return msg;
	}
	
	//internal calls
	public static void addInternalApiHit(String apiName, long tic){
		long time = System.currentTimeMillis()-tic;
		if (time > internalApiErrorThreshold){
			if (internalApiErrorTime.containsKey(apiName)){
				internalApiErrorHits.get(apiName).incrementAndGet();
				internalApiErrorTime.get(apiName).addAndGet(time);
			}else{
				internalApiErrorHits.put(apiName, new AtomicInteger());
				internalApiErrorHits.get(apiName).incrementAndGet();
				internalApiErrorTime.put(apiName, new AtomicLong());
				internalApiErrorTime.get(apiName).addAndGet(time);
			}
		}else{
			if (internalApiTime.containsKey(apiName)){
				internalApiHits.get(apiName).incrementAndGet();
				internalApiTime.get(apiName).addAndGet(time);
			}else{
				internalApiHits.put(apiName, new AtomicInteger());
				internalApiHits.get(apiName).incrementAndGet();
				internalApiTime.put(apiName, new AtomicLong());
				internalApiTime.get(apiName).addAndGet(time);
			}
		}
	}
		
	//external APIs
	public static void addExternalApiHit(String apiName, long tic){
		long time = System.currentTimeMillis()-tic;
		if (time > externalApiErrorThreshold){
			if (externalApiErrorTime.containsKey(apiName)){
				externalApiErrorHits.get(apiName).incrementAndGet();
				externalApiErrorTime.get(apiName).addAndGet(time);
			}else{
				externalApiErrorHits.put(apiName, new AtomicInteger());
				externalApiErrorHits.get(apiName).incrementAndGet();
				externalApiErrorTime.put(apiName, new AtomicLong());
				externalApiErrorTime.get(apiName).addAndGet(time);
			}
		}else{
			if (externalApiTime.containsKey(apiName)){
				externalApiHits.get(apiName).incrementAndGet();
				externalApiTime.get(apiName).addAndGet(time);
			}else{
				externalApiHits.put(apiName, new AtomicInteger());
				externalApiHits.get(apiName).incrementAndGet();
				externalApiTime.put(apiName, new AtomicLong());
				externalApiTime.get(apiName).addAndGet(time);
			}
		}
	}

}
