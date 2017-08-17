package de.bytemind.core.users;

import de.bytemind.core.tools.Debugger;

/**
 * Handle user ID types.
 * 
 * @author Florian Quirin
 *
 */
public class IdHandler {
	
	//Statics
	public static String user_id_prefix = "uid";	//prefix used when generating user ids or checking them, can be overwritten by e.g. a server that handles user accounts 
	
	/**
	 * Valid IDs to authenticate the user.
	 */
	public class Type {
		public static final String uid = "uid";
		public static final String email = "email";
		public static final String phone = "phone";
	}

	/**
	 * Auto-detect user id type (email, uid, phone?) 
	 * @return id type or empty string
	 */
	public static String autodetectType(String id){
		id = clean(id);
		if (id.matches("^" + user_id_prefix + "\\d+")){
			return Type.uid;
		}else if(id.matches(".*\\w+\\@.*\\..*\\w+")){
			return Type.email;
		}else if (id.matches("(\\+|\\-|)\\d+")){
			return Type.phone;
		}else{
			Debugger.println("ID type - autodetectType(...) failed! Id: " + id, 1);
			return "";
		}
	}
	
	/**
	 * Clean up the ID before trying to store or match it. Throws error on "-" and empty.
	 */
	public static String clean(String id){
		id = id.replaceAll("\\s+", "").trim().toLowerCase().trim();
		if (id.isEmpty() || id.equals("-")){
			throw new RuntimeException("cleanID(..) reports invalid ID: " + id);
		}else{
			return id;
		}
	}
}
