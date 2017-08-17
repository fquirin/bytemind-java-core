package de.bytemind.core.client;

import de.bytemind.core.users.RemoteAuthentication;

/**
 * Some default settings used in clients. Usually set during client or server start and set via configuration file.
 * 
 * @author Florian Quirin
 *
 */
public class ClientDefaults {
	
	public static final String client_info = "desktop_browser_v1.0";			//in case the client does not submit the info behave like this.
	public static String authentication_module = RemoteAuthentication.class.getCanonicalName();		//default authentication module
	public static String auth_endpoint_url = "http://localhost:8001/authentication";				//default authentication end-point

}
