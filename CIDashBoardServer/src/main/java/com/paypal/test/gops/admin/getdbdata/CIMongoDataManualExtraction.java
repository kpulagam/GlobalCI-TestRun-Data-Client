package com.paypal.test.gops.admin.getdbdata;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class CIMongoDataManualExtraction {

	private static final MongoClientOptions options = MongoClientOptions
			.builder().connectionsPerHost(100).build();
	private static MongoClient client = new MongoClient(new ServerAddress(
			"10.244.180.225", 27017), options);

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		Scanner input = null;

		try {

			CIMongoDataManualExtractionDAO mongoObject = new CIMongoDataManualExtractionDAO(
					"MetricsDB");
			TestRunFIleHandler fileHelper = new TestRunFIleHandler();
			fileHelper.clearScreen();

			input = new Scanner(System.in);

			System.out.println("Enter the suiteName: ");
			String suiteName = input.nextLine();
			System.out
					.println("Are you Looking for last 5 run's failures Options: 'Yes'or 'No'");
			String initialDecision = input.nextLine();
			List<Document> lastFiveRunFailureList = null;

			Date currentDate = new Date();
			File testRunFile = fileHelper.createFile();

			if (initialDecision.equalsIgnoreCase("Yes")) {
				lastFiveRunFailureList = mongoObject.getMultipleRunsFailures(5,
						client, suiteName);

				if (lastFiveRunFailureList != null) {

					String fileHeader = currentDate.toString();

					fileHelper.writeIntoFile(lastFiveRunFailureList,
							fileHeader, testRunFile, suiteName);

				}

			} else {
				System.out
						.println("Are you looking for latest test run failures Options: 'Yes'or 'No'");
				String secondDecision = input.nextLine();

				if (secondDecision.equalsIgnoreCase("Yes")) {
					String fileHeader = currentDate.toString();
					fileHelper.writeIntoFile(mongoObject.getFailuresForBuildId(
							client, suiteName,
							mongoObject.getLatestBuildID(suiteName, client)),
							fileHeader, testRunFile, suiteName);

				} else if (secondDecision.equalsIgnoreCase("No")) {
					System.out
							.println("Are you looking for test failures for a specific build? Options: 'Yes'or 'No'");
					String thirdDecision = input.nextLine();
					if (thirdDecision.equalsIgnoreCase("Yes")) {
						System.out
								.println("Enter the build ID for which you need the faulure for(Enter Numeric): ");
						String userBuildId = input.nextLine();
						System.out
								.println("Getting the results for the given build ID: ");
						String fileHeader = currentDate.toString();
						fileHelper.writeIntoFile(mongoObject
								.getFailuresForBuildId(client, suiteName,
										Integer.parseInt(userBuildId)),
								fileHeader, testRunFile, suiteName);

					} else {
						System.out
								.println("Do you want failures for more than 5 runs? Options: 'Yes'or 'No'");
						String finalDecision = input.nextLine();
						if (finalDecision.equalsIgnoreCase("Yes")) {
							String fileHeader = currentDate.toString();
							System.out
									.println("Please enter the number of test runs to be compared (Enter Numeric):");
							fileHelper
									.writeIntoFile(mongoObject
											.getMultipleRunsFailures(
													input.nextInt(), client,
													suiteName), fileHeader,
											testRunFile, suiteName);

						} else {
							System.out
									.println("You have exhausted all the options! Please initiate again,Good Bye!");
							Thread.sleep(3000);
						}
					}

				}

			}

			System.out
					.println("Use the below link to access the file that is generated");
			System.out.println(testRunFile.toURI().toURL());
			Thread.sleep(10000);

		} catch (Exception e) {

			System.out
					.println("Oops! Some error occured processing your request, Apologies. Please give it a shot again!");
			e.printStackTrace();
			Thread.sleep(5000);

		}

		input.close();

		client.close();

	}

}
