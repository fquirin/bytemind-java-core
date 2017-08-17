package de.bytemind.core.users;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.bytemind.core.tools.Is;
import de.bytemind.core.tools.JSON;

/**
 * Basic info that the authentication methods have to produce after the "upgrade" method.
 * 
 * @author Florian Quirin
 *
 */
public class AccountBasicInfo {
	
	public static final String EMAIL = "Email"; 		//upper-case because its an ID type
	public static final String PHONE = "Phone";			//upper-case because its an ID type
	public static final String LANGUAGE = "language";
	public static final String NAME = "name";
	public static final String ROLES = "roles";
	public static final String MORE = "more";

	String email;
	String phone;
	String language;
	JSONObject userName;
	JSONArray userRoles;
	
	JSONObject more;
	
	public AccountBasicInfo(){}
	
	@SuppressWarnings("unchecked")
	public AccountBasicInfo(JSONObject basicInfoJson){
		basicInfoJson.forEach((keyO, value) -> {
			String key = (String) keyO;
			switch(key){ 
	        case EMAIL: 
	        	email = (String) value; 
	            break;
	        case PHONE: 
	            phone = (String) value; 
	            break;
	        case LANGUAGE: 
	            language = (String) value; 
	            break;
	        case NAME: 
	            userName = (JSONObject) value; 
	            break;
	        case ROLES:
	        	userRoles = (JSONArray) value;
	        	break;
	        default:
	        	if (more == null){
	        		more = new JSONObject();
	        	}
	        	JSON.put(more, key, value); 
	        } 
		});
	}
	
	public void addMore(JSONObject more){
		this.more = more;
	}
	
	public JSONObject exportJson(){
		JSONObject json = new JSONObject();
		
		if (Is.notNullOrEmpty(email)) JSON.put(json, EMAIL, email);
		if (Is.notNullOrEmpty(phone)) JSON.put(json, PHONE, phone);
		if (Is.notNullOrEmpty(language)) JSON.put(json, LANGUAGE, language);
		if (Is.notNullOrEmpty(userName)) JSON.put(json, NAME, userName);
		if (Is.notNullOrEmpty(userRoles)) JSON.put(json, ROLES, userRoles);
		
		if (Is.notNullOrEmpty(more)) JSON.put(json, MORE, more);
		
		return json;
	}
}
