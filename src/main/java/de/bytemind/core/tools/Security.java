package de.bytemind.core.tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

/**
 * Methods to help encrypt stuff etc.
 * 
 * @author Florian Quirin
 *
 */
public class Security {
	
	/**
	 * Convert byte arrays like the generated md5 hashes etc to hex encoded string. 
	 * @param hash - byte array hash
	 * @return - hex encoded string
	 */
	public static String bytearrayToHexString(byte[] hash){
		String check = Hex.encodeHexString(hash);
		return check;
	}
	/**
	 * Convert a hex encoded string of bytes to bytes again. 
	 * @param s - hex string that was a byte array
	 * @return - byte array
	 */
	public static byte[] hexToByteArray(String s) {
	    return DatatypeConverter.parseHexBinary(s);
	}
	
	/**
	 * Returns a type 4 pseudo random UUID.
	 * @return
	 */
	public static String getRandomUUID(){
		return UUID.randomUUID().toString();
	}
	
	/**
	 * Simply hash a string with md5 algorithm.
	 * @param input - string to hash
	 * @return hashed byte array
	 * @throws Exception 
	 */
	public static byte[] getMD5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes("UTF-8"));	// Change this to "UTF-16" if needed
        byte[] digest = md.digest();
		return digest;
    }
	
	/**
	 * Simply hash a string with sha256 algorithm.
	 * @param data - input string
	 * @return hashed byte array
	 * @throws Exception
	 */
	public static byte[] getSha256(String data) throws Exception  {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data.getBytes(StandardCharsets.UTF_8)); 	//Change this to "UTF-16" if needed
		byte[] digest = md.digest();
		return digest;
	}
	
	/**
	 * HmacSHA256 encryption.
	 * @param data - input string 
	 * @param key - secret key to hash
	 * @return byte array with hashed string+key
	 * @throws Exception
	 */
	public static byte[] HmacSHA256(String data, byte[] key) throws Exception  {
	     String algorithm="HmacSHA256";
	     Mac mac = Mac.getInstance(algorithm);
	     mac.init(new SecretKeySpec(key, algorithm));
	     return mac.doFinal(data.getBytes("UTF-8"));
	}

	
	/**
	 * Hash the submitted password. This must be done by all the clients in the same way! It is more of a first step to guarantee
	 * the user that his password is never saved in clear form. As it is implemented client-side as well it is not a secret and can
	 * be considered a simple code obfuscation to prevent an accidental glimpse at passwords.
	 * 
	 * @param pwd - password to hash
	 * @return
	 */
	public static String hashPassword_client(String pwd){
		try {
			return Security.bytearrayToHexString(Security.getSha256(pwd + "salty1"));
		} catch (Exception e) {
			throw new RuntimeException("simple password hashing failed! (client)", e);
		}
	}
	
	/**
	 * PBKDF2 algorithm with HmacSHA256 Hash.
	 * @param password
	 * @param salt
	 * @param iterations
	 * @param derivedKeyLength
	 * @throws Exception
	 */
	public static byte[] getEncryptedPassword(String password, byte[] salt,  int iterations,  int derivedKeyLength) throws Exception {
	    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength * 8);
	    SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	    return f.generateSecret(spec).getEncoded();
	}
	/**
	 * Generate a random salt.
	 * @param length - byte array length, e.g. 32
	 */
	public static byte[] getRandomSalt(int length){
		final Random r = new SecureRandom();
		byte[] salt = new byte[length];
		r.nextBytes(salt);
		return salt;
	}

	/**
	 * Get AWS signature key to connect to API.
	 * @param key
	 * @param dateStamp
	 * @param regionName
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static byte[] getAwsSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception  {
	     byte[] kSecret = ("AWS4" + key).getBytes("UTF-8");
	     byte[] kDate    = HmacSHA256(dateStamp, kSecret);
	     byte[] kRegion  = HmacSHA256(regionName, kDate);
	     byte[] kService = HmacSHA256(serviceName, kRegion);
	     byte[] kSigning = HmacSHA256("aws4_request", kService);
	     return kSigning;
	}

}
