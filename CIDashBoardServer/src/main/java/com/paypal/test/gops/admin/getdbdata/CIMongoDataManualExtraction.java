package com.paypal.test.gops.admin.getdbdata;

import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class CIMongoDataManualExtraction {
	
	final static MongoClientOptions options = MongoClientOptions.builder()
			.connectionsPerHost(100).build();
	final static MongoClient client = new MongoClient(new ServerAddress("10.244.180.225", 27017),
			options);
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CIMongoDataManualExtractionDAO mondoObject = new CIMongoDataManualExtractionDAO();
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the suiteName: ");
		String suiteName = input.nextLine();
		System.out.println("Are you Looking for last 5 run's failures");
		String initialDecision = input.nextLine();
		
		
		
		
			if(initialDecision.equalsIgnoreCase("Yes")){
				mondoObject.getLastFiveRunsFailures(client,suiteName);
			}
			else if(initialDecision.equalsIgnoreCase("No")) {
				System.out.println("Are you looking for latest test run failures");
				String secondDecision = input.nextLine();
				if(secondDecision.equalsIgnoreCase("Yes")){
					System.out.println("Hurrey");
					mondoObject.getFailuresForBuildId(client,suiteName);
				}
				else if(secondDecision.equalsIgnoreCase("No")){
					System.out.println("I cannot answer you!");
				}
				
				else{
					System.out.println("I guess you are entering wrong keys");
				}
			}
			
			else{
				System.out.println("I guess you are entering wrong keys");
			}
		
		
		
		input.close();
		
		
		client.close();
		
		
		
		
		
	
		

	}
	
	

}
