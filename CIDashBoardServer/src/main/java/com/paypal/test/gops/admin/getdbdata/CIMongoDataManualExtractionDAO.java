package com.paypal.test.gops.admin.getdbdata;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

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

	

	protected List<Bson> getLastFiveRunsFailures(MongoClient client, String suiteName
			) {
		List<Bson> list1 = new ArrayList<>();
		List<Bson> list2 = new ArrayList<>();
		List<Bson> list3 = new ArrayList<>();
		List<Bson> list4 = new ArrayList<>();
		List<Bson> list5 = new ArrayList<>();
		List<Bson> tmpList = new ArrayList<>();
		try {
			
			final MongoDatabase testRunDB = client.getDatabase("MetricsDB")
					.withReadPreference(ReadPreference.secondary());

			String collectionName = suiteName;
			Bson projection = new Document("ClassName", 1).append("_id", 0);
			int buildNumber = getLatestBuildID(collectionName, client);
			Bson filter1 = and(eq("BuildNumber", buildNumber),
					eq("Status", "Failed"));
			testRunDB.getCollection(collectionName).find(filter1)
					.projection(projection).into(list1);

			Bson filter2 = and(eq("BuildNumber", buildNumber - 1),
					eq("Status", "Failed"));
			testRunDB.getCollection(collectionName).find(filter2)
					.projection(projection).into(list2);

			Bson filter3 = and(eq("BuildNumber", buildNumber - 2),
					eq("Status", "Failed"));
			testRunDB.getCollection(collectionName).find(filter3)
					.projection(projection).into(list3);

			Bson filter4 = and(eq("BuildNumber", buildNumber - 3),
					eq("Status", "Failed"));
			testRunDB.getCollection(collectionName).find(filter4)
					.projection(projection).into(list4);

			Bson filter5 = and(eq("BuildNumber", buildNumber - 4),
					eq("Status", "Failed"));
			testRunDB.getCollection(collectionName).find(filter5)
					.projection(projection).into(list5);

			for (Bson b : list1) {
				if (list2.contains(list1.get(0))) {
					tmpList.add(b);

				}

			}

			list1.clear();

			for (Bson b : tmpList) {
				if (list3.contains(tmpList.get(0))) {

					list1.add(b);
				}
			}

			tmpList.clear();
			for (Bson b : list1) {
				if (list4.contains(list1.get(0))) {

					tmpList.add(b);
				}
			}
			list1.clear();

			for (Bson b : tmpList) {
				if (list5.contains(tmpList.get(0))) {
					list1.add(b);
				}
			}
			

		} catch (MongoException e) {
			System.out
					.println("There seems to be an issue with MongoDB connectivity");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list1;
	}

	private synchronized int getLatestBuildID(String suiteName, MongoClient client) {
		int buildId = 0;
		try {

			final MongoDatabase testRunDB = client.getDatabase("MetricsDB")
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
	
	protected List<Document> getFailuresForBuildId(MongoClient client, String collectionName) {
		
		List<Document> failureList = new ArrayList<Document>();
		try{
			final MongoDatabase testRunDB = client.getDatabase("MetricsDB")
					.withReadPreference(ReadPreference.secondary());

			
			Bson projection = new Document("ClassName", 1).append("_id", 0);

			Bson filter = and(eq("BuildNumber", getLatestBuildID(collectionName, client)),
					eq("Status", "Failed"));
			testRunDB.getCollection(collectionName).find(filter)
					.projection(projection).into(failureList);
			
			
		}catch(MongoException e){
			System.out.println("Encountered connectivity issue with Mongo DB");
			e.printStackTrace();
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return failureList;
		

	}

}