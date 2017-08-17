package de.bytemind.core.users;

import java.util.Map;

import org.json.simple.JSONObject;

import de.bytemind.core.client.ClientDefaults;
import de.bytemind.core.tools.Connectors;
import de.bytemind.core.tools.Converters;
import de.bytemind.core.tools.Debugger;

/**
 * Implementation of the Authentication interface using a remote server (given by ClientDefaults.authentication_API).
 *  
 * @author Florian Quirin
 *
 */
public class RemoteAuthentication implements Authentication{
	
	//Stuff
	private String userid = "";
	private int errorCode = 0;
	private AccountBasicInfo basicInfo;
	private int accessLevel = 0;
	
	//authenticate user
	@Override
	public boolean authenticate(JSONObject info) {
		String userid = (String) info.get("userId");
		String password = (String) info.get("pwd");
		String client = (String) info.get("client");
		
		//check client - client has influence on the password token that is used
		if (client == null || client.isEmpty()){
			client = ClientDefaults.client_info;
		}
		if (password == null || password.trim().isEmpty()) {
			Debugger.println("Password null or empty for user '" + userid + "': '" + password + "'", 1);
		}
		//make URL
		this.userid = userid;
		String url = ClientDefaults.auth_endpoint_url + "?KEY=" + (userid + ";" + password) + "&action=check" 
						+ "&client=" + client;
		//System.out.println("Auth. call: " + url); 			//debug
		
		//call server
		JSONObject response = Connectors.httpGET(url);
		//System.out.println(response.toJSONString()); 			//debug
		
		//Status?
		if (!Connectors.httpSuccess(response)){
			Debugger.println("No success in auth response for user '" + userid + "', returning false: " + response, 1);
			errorCode = 3; 			//connection error, wrong parameters?
			return false;
		}
		else{
			String result = (String) response.get("result");
			if (result.equals("fail")){
				Debugger.println("'fail' in auth response for user '" + userid + "', returning false: " + response, 1);
				errorCode = 2;		//authentication failed
				return false;
			}
			//should be fine now - get basic info about user
			
			//TODO: add other IDs? (email, phone)
			
			//ACCESS LEVEL - TODO: not yet fully implemented, is always 0 for access and -1 for no access.
			accessLevel = Converters.obj2int(response.get("access_level"), 0);
			
			basicInfo = new AccountBasicInfo((JSONObject) response.get("basic_info"));
			
			//DONE - note: basicInfo CAN be null, so check for it if you use it.
			errorCode = 0; 			//all fine
			return true;
		}
	}
	
	//get errorCode set during authenticate
	@Override
	public int getErrorCode() {
		return errorCode;
	}
	
	//get basic info acquired during account check
	@Override
	public Map<String, Object> getRawBasicInfo() {
		//TODO: THIS CLASS HAS NO RAW VERSION OF BASIC INFO
		return null;
	}
	
	@Override
	public AccountBasicInfo upgradeBasicInfo(Map<String, Object> rawBasicInfo) {
		//remote call always gives default basic info structure, so no need to upgrade
		return basicInfo;	
	}
	
	//get user ID
	@Override
	public String getUserID() {
		return userid;
	}

	//get user access level
	@Override
	public int getAccessLevel() {
		return accessLevel;
	}
	
	//------------------------------------------------------------------
	//TODO:

	@Override
	public void setRequestInfo(Object request) {
		//not required here yet		
	}

	@Override
	public boolean logout(String userid, String client) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logoutAllClients(String userid) {
		// TODO Auto-generated method stub
		return false;
	}
}
