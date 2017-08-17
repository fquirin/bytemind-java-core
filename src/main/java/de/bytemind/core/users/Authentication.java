package de.bytemind.core.users;

import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Interface for any user authentication service.
 * 
 * @author Florian Quirin
 *
 */
public interface Authentication {
	
	/**
	 * Any authentication request coming from the network might include additional info that is needed besides user name and password.
	 * With this method you can submit any object that might contain this info and use it later during authentication.
	 * @param request - any object that contains the required info like a server request
	 */
	public void setRequestInfo(Object request);
	
	/**
	 * Classical user name, password combination. This might either check the values directly or communicate with an identity platform.
	 * During the process it is supposed to set the values for user ID, access level and error codes during any additional communication.
	 * @param info - JSONObject with:<br>
	 * userId - user name or empty string if only request info (header from setRequest()) is used<br>
	 * pwd - password<br>
	 * idType - type of ID, e.g. "uid" or "email"<br>
	 * client - client info for token path<br>
	 * @return true/false
	 */
	public boolean authenticate(JSONObject info);
	
	/**
	 * Typically a website will use the token issued during the last log-in procedure to authenticate the user. Logout() should 
	 * make this token invalid thus making further use of the token impossible on any machine.  
	 * @param userid - id to log out
	 * @param client - depending on the client different tokens can be used  
	 * @return
	 */
	public boolean logout(String userid, String client);
	
	/**
	 * Typically a website or app will use a token issued during the last log-in procedure to authenticate the user. logoutAllClients() should 
	 * make all tokens invalid thus making further use of the token impossible on any machine.  
	 * @param userid - id to log out
	 * @param client - depending on the client different tokens can be used  
	 * @return
	 */
	public boolean logoutAllClients(String userid);
	
	/**
	 * After a successful authentication the class obtains the user ID associated with the user. It is the unique identifier to access any database info etc.
	 * @return unique user ID (-1 if the user was not authenticated)
	 */
	public String getUserID();
	
	/**
	 * After a successful authentication the class obtains the user access level. Access levels control information flow and might be increased by
	 * a multi-factor authentication (biometry, location, device info etc.)
	 * @return authentication level starting at -1 (no access) and 0 (lowest, with very limited access to personal info)
	 */
	public int getAccessLevel();
	
	/**
	 * During authentication some basic info of the user, like user name etc. can be obtained and passed down to the token/user.
	 * Info is stored in this HashMap. The stored info is usually in an arbitrary raw format to save some processing time.
	 * Use upgradeBasicInfo to convert it to default format.
	 */
	public Map<String, Object> getRawBasicInfo();
	
	/**
	 * Upgrade raw basic info to default basic info. Usually this happens during the transition of a fast authentication "Token" to the more complex "Account" class.
	 * @param rawBasicInfo - basic info obtained during authentication
	 */
	public AccountBasicInfo upgradeBasicInfo(Map<String, Object> rawBasicInfo);
	
	/**
	 * Returns an error code set during authentication to give you more info about what went wrong.
	 * Use:<br>
	 * 0 - no errors <br>
	 * 1 - communication error (like server did not respond) <br>
	 * 2 - access denied (due to wrong credentials or whatever reason) <br>
	 * 3 - might be 1 or 2 (where 2 can also be due to wrong parameters)<br>
	 * 4 - unknown error <br>
	 * 5 - during registration/requestPasswordChange: user existence check failed; during createUser/changePassword: invalid token or time stamp<br>
	 * 6 - password format invalid<br>
	 * 7 - UID generation or storing failed<br>
	 * 
	 * @return integer error code
	 */
	public int getErrorCode();

}
