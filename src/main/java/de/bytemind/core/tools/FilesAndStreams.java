package de.bytemind.core.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

public final class FilesAndStreams {
	
	private FilesAndStreams() {
	}

	/**
	 * Collect all data of an InputStream to a string. 
	 */
	public static String getStringFromStream(InputStream stream) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			return response.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Get an ArrayList of "File"s from a directory path.
	 * @param directoryName - path to directory
	 * @param files - ArrayList of files to populate
	 * @param doSubfolders - include sub-folders?
	 * @return list or null
	 */
	public static ArrayList<File> directoryToFileList(String directoryName, ArrayList<File> files, boolean doSubfolders) {
		File directory = new File(directoryName);
	
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    if (fList == null){
	    	return null;
	    }
	    //System.out.println(directory.list().length);		//debug
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	            //System.out.println(file.toString());		//debug
	        } else if (file.isDirectory() & doSubfolders) {
	        	//listAllFiles(file.getAbsolutePath(), files, doSubfolders);
	        	directoryToFileList(file.getPath(), files, doSubfolders);
	        }
	    }
		return files;
	}
	
	/**
	 * Save settings to properties file.
	 * @param config_file - path and file
	 * @param config - Properties with settings to store
	 */
	public static void saveSettings(String config_file, Properties config) throws Exception{
		OutputStream out =null;
		File f = new File(config_file);
        out = new FileOutputStream( f );
        config.store(out, null);
        out.flush();
        out.close();
	}
	/**
	 * Load settings from properties file and return Properties.
	 * @param config_file - path and file
	 */
	public static Properties loadSettings(String config_file) throws Exception{
		BufferedInputStream stream=null;
		Properties config = new Properties();
		stream = new BufferedInputStream(new FileInputStream(config_file));
		config.load(stream);
		stream.close();
		return config;
	}

}
