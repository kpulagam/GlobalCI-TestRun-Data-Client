package com.paypal.test.gops.admin.cilistener;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;


	
	public class MongoDAO {

		/**
		 * @param args
		 */
		static final Logger log = Logger.getLogger("dbDataLogger");
		
		final MongoClientOptions options = MongoClientOptions.builder()
				.connectionsPerHost(100).build();
		final MongoClient client = new MongoClient(new ServerAddress("10.244.180.225", 27017),
				options);
	
		final MongoDatabase testRunDB = client.getDatabase("MetricsDB").withReadPreference(
				ReadPreference.secondary());
		final MongoDatabase suiteDB = client.getDatabase("MetricsDB").withReadPreference(
				ReadPreference.secondary());
		
		final String buildDataCollectionName = "buildDataCollection";
		private int buildNumber;
		private String testRunStartTime;
		
		private MongoCollection<Document> testRunDataDocument, buildDataDocument; 
		
		
		
	
		
		
		
		
		public MongoDAO(String collectionName) {
			
				Date date = new Date();
				testRunStartTime = date.toString();
			
			try{				
				
				if(collectionName.contains(" ")){
					collectionName = collectionName.replace(" ", "");
				}
				checkBuildInformation(collectionName);
				log.debug("Collection being used is: "+collectionName);
				testRunDataDocument = testRunDB.getCollection(collectionName);	
							
			}catch(MongoException e){
				
				log.debug("Error occurred in Mongo DB connection",e);
				testRunDB.drop();
				client.close();
			}catch(Exception e){
				
				log.debug("Encountered generic exception while creating initial document",e);
				testRunDB.drop();
				client.close();
			}
		
				
				
			
			
			
		}
		protected synchronized void writeIntoDb(Document doc, String ClassName){
			
			try{

				Bson filter = and(eq("ClassName",ClassName),eq("BuildNumber",buildNumber));
				
				List<Document> all = testRunDataDocument.find(filter).into(new ArrayList<Document>());
				
				log.debug(all.size());
				if(all.size()>0){
					testRunDataDocument.replaceOne(filter, doc.append("BuildNumber", buildNumber));
					
					log.debug("Doc Updated");
				}
				else{
					testRunDataDocument.insertOne(doc.append("BuildNumber", buildNumber));
					log.debug("Doc Inserted");
					
				}
				
				
			}catch(MongoException e){
				
				log.debug("Mongo Error occurred while writing in Mongo DB for class: "+ClassName,e);
			}
			catch(Exception e){
				
				log.debug("Encountered generic exception while writing in Mongo DB for class: "+ClassName,e);
			}
			
			
			
			
		}
		
		private void checkBuildInformation(String collectionName){
			
			try{
				
				buildDataDocument = suiteDB.getCollection(buildDataCollectionName);
				Bson filter = eq("suiteName",collectionName);
				MongoCursor<Document> newCurser = buildDataDocument.find(filter).iterator();
				
				List<Document> all= buildDataDocument.find(filter).into(new ArrayList<Document>());
				log.debug(all);
				if(all.isEmpty()){
					buildDataDocument.insertOne(new Document("suiteName", collectionName).append("buildNumber", 1).append("testRunStartDate", testRunStartTime).append("testRunEndDate", null).append("istestRunCompleted", false));
					this.buildNumber = 1;
					log.debug("Suite executed for first time");
					
				}
				else{
					Date date = new Date();
					buildDataDocument.find(filter);
					this.buildNumber = newCurser.tryNext().getInteger("buildNumber")+1;					 
					log.debug(buildNumber);
					buildDataDocument.updateOne(filter, new Document("$set",new Document("testRunStartDate",date.toString())),new UpdateOptions().upsert(false));
					buildDataDocument.updateOne(filter, new Document("$inc",new Document("buildNumber",1)),new UpdateOptions().upsert(false));
					buildDataDocument.updateOne(filter, new Document("$set",new Document("istestRunCompleted",false)),new UpdateOptions().upsert(false));
					buildDataDocument.updateOne(filter, new Document("$set",new Document("testRunEndDate",null)),new UpdateOptions().upsert(false));
					
				}
				
				
			}
			catch(MongoException e){
				
				log.debug("Encountered Mongo Error in checking the build Information from buildDataCollection",e);
			}
			catch(Exception e){
				
				log.debug("Encountered generic exception in checking the build Information buildDataCollection",e);
			}
		
		}
		
		protected void completeTest(String collectionName){
			Date date = new Date();
			Bson filter = and(eq("suiteName",collectionName.replace(" ", "")),eq("buildNumber",this.buildNumber));
			log.debug(collectionName);
			log.debug(this.buildNumber);
			log.debug(date.toString());
			buildDataDocument.updateOne(filter, new Document("$set",new Document("testRunEndDate",date.toString())),new UpdateOptions().upsert(false));
			buildDataDocument.updateOne(filter, new Document("$set",new Document("istestRunCompleted",true)),new UpdateOptions().upsert(false));
			
		}
		
		

	}
	


