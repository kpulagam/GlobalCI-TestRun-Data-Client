package com.paypal.test.gops.admin.getdbdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class CIMongoDataManualExtraction {

	private static MongoClientOptions options;
	private static MongoClient client;
	private static int maxConnectionsPerHost;
	private static String dbIPAddress;
	private static int dbPortNumber;
	private static String dbName;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		getDBConfigValues();
		
		options = MongoClientOptions.builder()
				.connectionsPerHost(maxConnectionsPerHost).build();
		client = new MongoClient(new ServerAddress(
				dbIPAddress, dbPortNumber), options);
		
		
		CIMongoDataManualExtractionDAO mongoObject = new CIMongoDataManualExtractionDAO(dbName);
		TestRunFIleHandler fileHelper = new TestRunFIleHandler();
		fileHelper.clearScreen();
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter the suiteName: ");
		String suiteName = input.nextLine();
		System.out.println("Are you Looking for last 5 run's failures");
		String initialDecision = input.nextLine();
		List<Document> lastFiveRunFailureList = mongoObject
				.getLastFiveRunsFailures(client, suiteName);
		Date currentDate = new Date();
		File testRunFile = fileHelper.createFile();

		if (initialDecision.equalsIgnoreCase("Yes")) {

			if (lastFiveRunFailureList != null) {

				String fileHeader = "************" + currentDate.toString()
						+ "************";
				fileHelper.writeIntoFile(lastFiveRunFailureList, fileHeader,
						testRunFile);

			}

		}

		else if (initialDecision.equalsIgnoreCase("No")
				|| lastFiveRunFailureList == null) {
			System.out.println("Are you looking for latest test run failures");
			String secondDecision = input.nextLine();
			if (secondDecision.equalsIgnoreCase("Yes")) {
				String fileHeader = "************" + currentDate.toString()
						+ "************";
				fileHelper.writeIntoFile(
						mongoObject.getFailuresForBuildId(client, suiteName),
						fileHeader, testRunFile);

			} else if (secondDecision.equalsIgnoreCase("No")) {
				System.out.println("I cannot answer you!");
			}

			else {
				System.out.println("I guess you are entering wrong keys");
			}
		}

		else {
			System.out.println("I guess you are entering wrong keys");
		}

		input.close();

		client.close();
		System.out.println("Use the below link to access the file that is generated");
		System.out.println(testRunFile.toURI().toURL());
		

	}
	
	private static void getDBConfigValues(){
		FileInputStream ip;
		try {
			ip = new FileInputStream("src/main/resources/MongoConfig.properties");
			Properties prop = new Properties();
			prop.load(ip);
			maxConnectionsPerHost = Integer.parseInt(prop.get("connectionsLimit").toString());
			dbIPAddress = prop.get("dbServerIP").toString();
			dbPortNumber = Integer.parseInt(prop.get("dbPortNumber").toString());
			dbName = prop.get("dbName").toString();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to read data base properties file!");
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e){
			System.out.println("Unable to fetch data base configuration");
			e.printStackTrace();
			
		}
		
		
		
	}

}
