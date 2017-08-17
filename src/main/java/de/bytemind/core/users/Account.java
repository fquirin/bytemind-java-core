package de.bytemind.core.users;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import de.bytemind.core.client.ClientDefaults;
import de.bytemind.core.tools.ClassBuilder;
import de.bytemind.core.tools.Converters;
import de.bytemind.core.tools.Debugger;
import de.bytemind.core.tools.Is;
import de.bytemind.core.tools.JSON;
import de.bytemind.core.tools.Security;

/**
 * This class holds info about the user account acquired during authentication.
 * It is the light version of the "User" class in assistant-API so I gave it a new name to avoid possible future conflicts.
 * Keep it slim and light!
 * 
 * @author Florian Quirin
 */
public class Account {

	//service that is able to perform user authentication
	private final Authentication authService = (Authentication) ClassBuilder.construct(ClientDefaults.authentication_module);
	
	//account basic user data
	private String userId = "";				//unique ID of user
	private String email = "";				//other IDs (number, email, whatever ...), acquired during authentication, must not be changed afterwards!
	private String phone = "";				//...
	private String client = "";				//client info
	private int accessLevel = -1;			//level of access depending on user account authentication (0 is the lowest, -1 is no-auth)
	private JSONObject userName;			//JSON name with e.g. "nick":"Jim", "first":"James", "last":"Last"
	private String userNameShort = "";		//short version of name, ready to use :-)
	private String language = "en";			//user account language (ISO-639 code)
	private JSONArray userRoles;			//user roles managing certain access rights
	//more
	private JSONObject info; 	//info for everything that's not in the default basics
	
	//for auth:
	private String KEY = "";
	private String UID = "";
	private String PWD = "";
	private String idType = "";
	private int errorCode;						//errorCode passed down from authenticator 
	
	/**
	 * Default constructor with KEY and client.
	 */
	public Account(String key, String idType, String client){
		this.KEY = key;
		this.idType = idType;
		this.client = client;
	}
	/**
	 * Optional constructor. Try to avoid when possible as it is much costlier.
	 */
	public Account(String uid, String pwd, String idType, String client){
		this.UID = uid;
		this.PWD = pwd;
		this.idType = idType;
		this.client = client;
	}
	/**
	 * Helper to use account internally. 
	 */
	public Account(String uid){
		this.userId = uid;
	}
	/**
	 * Helper to use account internally. 
	 */
	public Account(String uid, int accessLevel, String client){
		this.userId = uid;
		this.accessLevel = accessLevel;
		this.client = client;
	}
	
	/*
	public void importEssentials(String userId, int accessLevel, String client){
		this.userId = userId;
		this.accessLevel = accessLevel;
		this.client = client;
	}
	*/
	
	/**
	 * "Unpack" basic (raw) info and map it to the account fields. This method is automatically called during "authenticate"
	 * @param basicInfo - info obtained during "fast" authentication
	 */
	public void mapBasicInfo(AccountBasicInfo basicInfo){
		//Email
		if (Is.notNullOrEmpty(basicInfo.email)){
			email = basicInfo.email;
		}

		//Phone
		if (Is.notNullOrEmpty(basicInfo.phone)){
			phone = basicInfo.phone;
		}
				
		//Language
		if (Is.notNullOrEmpty(basicInfo.language)){
			language = basicInfo.language;
		}
		
		//Name
		if (Is.notNullOrEmpty(basicInfo.userName)){
			userName = basicInfo.userName;
			userNameShort = getShortUserName();
		}
		
		//Roles
		if (basicInfo.userRoles != null && !basicInfo.userRoles.isEmpty()){
			userRoles = basicInfo.userRoles;
		}
		
		//More
		if (Is.notNullOrEmpty(basicInfo.more)){
			info = basicInfo.more;
		}
	}
	
	/**
	 * Return unique user ID.
	 */
	public String getUserID(){
		return userId;
	}
	/**
	 * Return email user ID
	 */
	public String getEmail(){
		return email;
	}
	/**
	 * Return phone user ID.
	 */
	public String getPhone(){
		return phone;
	}
	
	/**
	 * Return client info used during auth.
	 */
	public String getClientInfo(){
		return client;
	}

	/**
	 * Name data string, e.g. "&lt;nickN>Jim&lt;firstN>James&lt;lastN>Last";
	 */
	public JSONObject getUserName() {
		return userName;
	}
	
	/**
	 * Simple name to address the user in a UI or something.
	 */
	public String getUserNameShort() {
		return userNameShort;
	}
	
	/**
	 * Get user language.
	 */
	public String getPreferredLanguage(){
		return language;
	}
	
	/**
	 * Return user access level.
	 */
	public int getAccessLevel(){
		return accessLevel;
	}
	
	/**
	 * Info stored in account but not part of the basics.
	 */
	public JSONObject getMoreInfo(){
		return info;
	}
	/**
	 * Info stored in account but not in basic entries.
	 */
	public Object getMoreInfo(String key){
		return info.get(key);
	}
	/**
	 * Add more info. Used e.g. during account loading for everything thats not in the basics.
	 */
	public void setMoreInfo(String key, Object obj){
		JSON.put(info, key, obj);
	}

	/**
	 * User's access permissions.
	 */
	public JSONArray getUserRoles() {
		return userRoles;
	}

	public boolean hasRole(String roleName){
		return userRoles != null && userRoles.contains(roleName);
	}
	public boolean hasRole(Role roleName){
		return userRoles != null && userRoles.contains(roleName.name());
	}
	
	/**
	 * Get a short version of the user's name like his nick name (if defined).
	 * @return nick name, first name, last name or default name (Boss) 
	 */
	private String getShortUserName(){
		String name = "Boss";
		if (userName == null || userName.isEmpty()){
			return name;
		}
		String nick = (String) userName.get("nick");
		String first = (String) userName.get("first");
		String last = (String) userName.get("last");
		
		//check if any of the data is available in the order nick, first, last
		if (nick!=null && !nick.isEmpty()){
			name = nick;
		}else if (first!=null && !first.isEmpty()){
			name = first;
		}else if (last!=null && !last.isEmpty()){
			name = last;
		}
		return name;
	}
	
	/**
	 * Authenticate the user. Copies basic user info to this class on successful authentication. 
	 * @return true or false
	 */
	public boolean authenticate(){
		//get parameters
		String key = this.KEY;
		if (Is.nullOrEmpty(key)){
			String guuid = this.UID;
			String pwd = this.PWD;
			if (Is.notNullOrEmpty(guuid) && Is.notNullOrEmpty(pwd)){
				key = guuid + ";" + Security.hashPassword_client(pwd);
			} 
		}
		if (Is.nullOrEmpty(client)){
			client = ClientDefaults.client_info;
		}
		//check
		if (Is.notNullOrEmpty(key)){
			String[] up = key.split(";",2);
			if (up.length == 2){
				String username = up[0].toLowerCase();
				String password = up[1];
				//call
				JSONObject authInfo = JSON.make("userId", username,
											"pwd", password,
											"idType", (Is.nullOrEmpty(idType)? IdHandler.autodetectType(username) : idType),
											"client", client);
				boolean success = authService.authenticate(authInfo);
				errorCode = authService.getErrorCode();
				
				//get basic info
				if (success){
					userId = authService.getUserID();
					accessLevel = authService.getAccessLevel();
					
					AccountBasicInfo basicInfo = authService.upgradeBasicInfo(authService.getRawBasicInfo());
					mapBasicInfo(basicInfo);
				}
				return success;
			
			//ERROR
			}else{
				errorCode = 2;
				Debugger.println("AUTHENTICATION FAILED! Due to wrong KEY format: '" + key + "'", 1);
				return false;
			}
		
		//ERROR
		}else{
			errorCode = 2;
			Debugger.println("AUTHENTICATION FAILED! Due to missing KEY", 1);
			return false;
		}
	}
	
	/**
	 * Error code passed down from authentication.
	 * 0 - no errors <br>
	 * 1 - communication error (like server did not respond)	<br>
	 * 2 - access denied (due to wrong credentials or whatever reason)	<br>
	 * 3 - might be 1 or 2 whereas 2 can also be that the parameters were wrong<br>
	 * 4 - unknown error <br>
	 * 5 - during registration: user already exists; during createUser: invalid token or time stamp	<br>
	 * @return
	 */
	public int getErrorCode(){
		return errorCode;
	}
	
	/**
	 * Export user account data to JSON string.
	 */
	public JSONObject exportJSON(){
		JSONObject account = new JSONObject();
		JSON.add(account, "userId", userId);
		JSON.add(account, "email", email);
		JSON.add(account, "phone", phone);
		JSON.add(account, "client", client);
		JSON.add(account, "userName", userName);
		JSON.add(account, "accessLevel", accessLevel);
		if (userRoles != null && !userRoles.isEmpty()){
			JSON.add(account, "userRoles", userRoles);
		}
		JSON.add(account, "language", language);
		if (Is.notNullOrEmpty(info)) 		JSON.add(account, "moreInfo", info);
		return account;
	}
	/**
	 * Import account from JSONObject. Make sure user was empty before!
	 */
	public void importJSON(JSONObject account){
		//ID, LVL, NAME
		userId = (String) account.get("userId");
		email = (String) account.get("email");
		phone = (String) account.get("phone");
		client = (String) account.get("client");
		accessLevel = Converters.obj2int(account.get("accessLevel"), -1);
		userName = (JSONObject) account.get("userName");
		userNameShort = getShortUserName();
		//pref. LANG
		language = (account.containsKey("language"))? ((String) account.get("language")) : "en";
		//ROLES
		userRoles = (JSONArray) account.get("userRoles");
		//INFO
		info = (account.containsKey("moreInfo"))? ((JSONObject) account.get("moreInfo")) : null;
	}

}
