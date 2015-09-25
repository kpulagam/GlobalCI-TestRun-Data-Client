package com.paypal.test.gops.admin.cilistener.databasebeans;

import org.bson.Document;

public interface ListnerDataDAO {

	
	public void writeIntoDb(Document doc, String ClassName);
	public void checkBuildInformation(String collectionName);
	public void completeTest(String collectionName);
}