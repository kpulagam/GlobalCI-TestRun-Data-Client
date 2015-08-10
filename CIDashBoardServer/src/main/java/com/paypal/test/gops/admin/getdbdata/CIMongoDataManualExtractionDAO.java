package com.paypal.test.gops.admin.getdbdata;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CIMongoDataManualExtractionDAO {

	/**
	 * @param args
	 * 
	 */

	private String dbName;

	CIMongoDataManualExtractionDAO(String dbName) {
		this.dbName = dbName;
	}

	protected synchronized int getLatestBuildID(String suiteName,
			MongoClient client) {
		int buildId = 0;
		try {

			final MongoDatabase testRunDB = client.getDatabase(dbName)
					.withReadPreference(ReadPreference.secondary());

			MongoCollection<Document> buildDataDocument = testRunDB
					.getCollection("buildDataCollection");
			Bson builDatafilter = eq("suiteName", suiteName);
			Bson buildDataProjection = new Document("istestRunCompleted", 1)
					.append("_id", 0);
			if (buildDataDocument.find(builDatafilter).iterator().hasNext()) {
				buildId = buildDataDocument.find(builDatafilter).first()
						.getInteger("buildNumber");

				if (buildDataDocument.find(builDatafilter)
						.projection(buildDataProjection).first()
						.getBoolean("istestRunCompleted").equals(false)) {
					System.err
							.println("There is a test run in progress, Hence Giving the build id of the previous run that has completed");
					buildId--;
				}

			} else {
				System.out
						.println("There is no test run with the given Suite Name");
			}

			System.out.println("The latest Build ID is: " + buildId);

		} catch (NullPointerException e) {
			System.out
					.println("Check the name of the suite file that you have provided");
			e.printStackTrace();
		} catch (MongoException e) {
			System.out.println("There is an issue with Mongo DB connection");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buildId;

	}

	protected List<Document> getMultipleRunsFailures(int numberOfRuns,
			MongoClient client, String suiteName) {
		List<Document> finalList = new ArrayList<>();
		if (suiteName.equals(null) || suiteName.equals(""))
			throw new NullPointerException("You should enter a suite name!");
		if (numberOfRuns < 1)
			throw new InvalidParameterException(
					"The number of test runs that you have entered is less than 1");

		final MongoDatabase testRunDB = client.getDatabase(dbName)
				.withReadPreference(ReadPreference.secondary());

		Bson projection = new Document("ClassName", 1).append("TestTagName", 1)
				.append("_id", 0);
		int buildNumber = getLatestBuildID(suiteName, client);

		if (numberOfRuns > buildNumber) {
			System.out
					.println("The number of test runs you have requested are"
							+ " greater that the testruns recorded, will compare results from all of the runs recorded!");
			numberOfRuns = buildNumber;

		}

		if (numberOfRuns == 1) {
			List<Document> singleList = new ArrayList<>();
			Bson oddfilter = and(eq("BuildNumber", 1), eq("Status", "Failed"));
			testRunDB.getCollection(suiteName).find(oddfilter)
					.projection(projection).into(singleList);
			finalList.addAll(singleList);

		}

		else if (numberOfRuns > 1) {
			int j = 1;
			List<Document> evenList = new ArrayList<>();
			List<Document> oddList = new ArrayList<>();

			while (j < numberOfRuns) {

				Bson oddfilter = and(eq("BuildNumber", buildNumber),
						eq("Status", "Failed"));
				testRunDB.getCollection(suiteName).find(oddfilter)
						.projection(projection).into(oddList);

				if (finalList.isEmpty()) {
					

					Bson evenfilter = and(eq("BuildNumber", --buildNumber),
							eq("Status", "Failed"));
					testRunDB.getCollection(suiteName).find(evenfilter)
							.projection(projection).into(evenList);

				} else {
					evenList.addAll(finalList);
					finalList.clear();

				}

				for (Document oddDoc : oddList) {
					for (Document evenDoc : evenList) {
						if (evenDoc.get("ClassName").equals(
								oddDoc.get("ClassName"))) {
							finalList.add(evenDoc);
						}

					}

				}

				j++;
				buildNumber--;

				oddList.clear();
				evenList.clear();
			}

		}

		else {
			System.out
					.println("Looks like you have entered invalid number of test runs to be analysed!");
		}

		return finalList;

	}

	protected List<Document> getFailuresForBuildId(MongoClient client,
			String suiteName, int buildID) {

		List<Document> failureList = new ArrayList<Document>();
		try {
			final MongoDatabase testRunDB = client.getDatabase(dbName)
					.withReadPreference(ReadPreference.secondary());

			Bson projection = new Document("ClassName", 1).append(
					"TestTagName", 1).append("_id", 0);

			Bson filter = and(eq("BuildNumber", buildID),
					eq("Status", "Failed"));
			testRunDB.getCollection(suiteName).find(filter)
					.projection(projection).into(failureList);

		} catch (MongoException e) {
			System.out.println("Encountered connectivity issue with Mongo DB");
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		}

		return failureList;

	}
}
