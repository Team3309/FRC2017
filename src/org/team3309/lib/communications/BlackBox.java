package org.team3309.lib.communications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * BlackBox Class
 * @author Rich Mayfield
 * @since 10/13/16
 * 
 * This class searches for a USB Mass Storage Drive connected to one of the ports on the RoboRio.
 * If found, this class provides methods to log data to a file that is created upon calling the initializeLog method.
 * 
 * How to use:
 * 1. Call initializeLog and pass it a String Array representing your header, a boolean for FMS or no FMS, and a boolean for verbose mode
 * 2. Use the logThis method to pass data into the BlackBox
 * 3. Use writeLog with no parameters to write the current BlackBox data to the file
 * 
 * Optional: use writeLog and pass it a String Array representing all your data. Be careful, this method must match your header or it will
 * be difficult to decipher data.
 * 
 * Features:
 * Automatically sort log files into directories by day.
 * Files are named by hour and minute in military time format.
 * Files have a"MATCH" suffix if you tell the BlackBox your're in a match
 * Files have a human-readable header.
 * If you forget to put a field in your header, the program will add it and write a new header line
 * 
 */

public class BlackBox {
	private static String rootPath = "/media/sda1/";
	private static String customLogPath = "Logs/";
	private String detailLogPath = "Data/";
	
	private static String fileName = "LogFile.txt";
	
	private static ArrayList<String> HEADER = new ArrayList<String>();

	private static PrintWriter writer;
	
	private static HashMap<String, Double> logHash = new HashMap<>();
	
	private static boolean LOG_STATUS = false;
	
	/**
	 * initialiazeLog Method 
	 * 
	 * @param headerArray A String Array containing header fields for the CSV file. These will be added to the HashMap for the log
	 * @param match A boolean that indicates if we're in a match or not True= FMS Match and False= No FMS. Affects how the logfile is named.
	 * @param verbose A boolean that 
	 */
	public static void initializeLog(String[] headerArray, boolean match, boolean verbose)
	{
		for(int i=0;i<headerArray.length;i++)
		{
			HEADER.add(headerArray[i]);
		}
		
		if(HEADER.get(0)!= "Timestamp")
		{
			if(HEADER.get(0).toLowerCase().contains("time")) //Probably called first field something to do with time, so fix it
			{
				//User entered a timestamp for first field, but in a non-standard form
				System.out.println("Next time call your first field 'Timestamp'");
				HEADER.set(0, "Timestamp"); //Set the first element to "Timestamp"
			}else
			{
				//User probably didn't enter a timestamp field at all
				HEADER.add(0, "Timestamp"); //Add a "Timestamp" field at the beginning 
				System.out.println("A proper header always begins with a Timestamp");
			}
		}
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat militaryTime = new SimpleDateFormat("HHMM");
		DateFormat dateFolderFormat = new SimpleDateFormat("ddMMMYYYY");
		
		final String TIMEZONE = "SS";
		dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
		timeFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
		militaryTime.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
		dateFolderFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
		
		Date date = new Date();
		
		System.out.println(dateFormat.format(date));
		
		//Organize folders by date by adding a date to the directory path
		customLogPath = customLogPath + dateFolderFormat.format(date) + "/";
		
		// Create a filename based on the hour and minute
		fileName = militaryTime.format(date);
		
		// Add a "Match" Suffix if competing
		if(match)
		{
			fileName = fileName + "MATCH";
		}
		
		fileName = fileName + ".csv"; // Create file extension for filename
		
		
		//Compose the full path and LogFile name
		String fullName = rootPath + customLogPath + fileName;
		
		File file = new File(fullName);
		
		file.getParentFile().mkdirs(); // Make directories if necessary

	
		//Open the log file
		try {
			writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) 
			{
				// Detect if there is no USB
				File f = new File(rootPath);
				if(f.exists() == false)
				{
					System.out.println("Error- Is there a Thumbdrive inserted?");
					System.out.println("BlackBox Disabled!");
					return; //Bail Out
				}
				// Detect if file not found
				System.out.println("File Error...BlackBox Disabled!");
				//e.printStackTrace();
				return; //Bail out bro!
			}
		
		LOG_STATUS = true; // Set a flag upon successful file creation
		
		/* Write header strings
		 * 
		 * The purpose of this section is to provide a human readable header for the log file
		 */
		writer.println("Team 3309 Log File");
		writer.print("Date: ");
		writer.println(dateFormat.format(date));
		writer.print("Time: ");
		writer.println(timeFormat.format(date));
		writer.println();
		writeHeader();
		writer.println();
		writer.flush();
		
	}
	
	public static void writeLog(String[] dataArray)
	{
		if(LOG_STATUS)
		{
			String dataString = ""; //Create a string to store data
			
			for(int i = 0; i<dataArray.length; i++)
			{
				dataString = dataString + dataArray[i] + ",";
			}
			//Delete the last comma
			dataString = dataString.substring(0, dataString.length()-1);
			
			writer.println(dataString);
			writer.flush();		
		}
	}
	
	public static void writeLog()
	{
		Date date = new Date();
		DateFormat stampFormat = new SimpleDateFormat("mm:ss.SSS");
		
		//Start the dataString with a timestamp
		String dataString = stampFormat.format(date) + ",";
		
		
		// Assemble a dataArray from a HashMap, start at 1 to skip the timestamp
		for(int i = 1;i<HEADER.size();i++)
		{
			Double value = logHash.get(HEADER.get(i));
			
			if(value == null) //Checks to see if there's a null value in that bucket
			{
				value = 0.0; //Set value to zero
				System.out.println("No value for '" + HEADER.get(i) + "'! Don't you want to log that?");
				logHash.put(HEADER.get(i), 0.0); //Put a value in there so we only throw the above error once
			}
			dataString = dataString + Double.toString(value) + ",";
		}
		
		//Delete the last comma
		dataString = dataString.substring(0, dataString.length()-1);
		
		if(LOG_STATUS)
		{
			writer.println(dataString);
		}
	}
	
	private static void writeHeader()
	{
		//Compose string from headerArray and build the logHash HashMap
		String headerString = "";
		
		for(int i = 0; i<HEADER.size(); i++)
		{
			headerString = headerString + HEADER.get(i) + ","; //Compose a header string
			
			if(!logHash.containsKey(HEADER.get(i)))	//Check to see if we already have a key in our HashMap
			{
				logHash.put(HEADER.get(i), null); // Initialize keys for the logHash
			}
		}
		//Delete the last comma from the headerString
		headerString = headerString.substring(0, headerString.length()-1);
		
		writer.println(headerString);
	}
	
	public static void logThis(String key, double value)
	{
		if(logHash.containsKey(key)) 		// Add the key and value to a HashMap if key exists
		{
			logHash.put(key, value);
		}else
		{
			logHash.put(key, value); 		// Add the key if it doesn't exist
			HEADER.add(key);	// Add the field to the header
			writeHeader();					// Re-write the header if parameter is added
			
			// Throw an error reminding to add to header upon initilization if we have to add to HashMap
			System.out.println("Logged Parameter '" + key + "' not found. Add this parameter to the Blackbox initilization.");
		}
	}
	
	
	
	public static void writeString(String myString)
	{
		if(LOG_STATUS)
		{
			writer.println(myString);
			writer.flush();
		}
	}
}