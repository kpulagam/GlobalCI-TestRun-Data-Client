package com.paypal.test.gops.admin.getdbdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.Document;

public class TestRunFIleHandler {
	
	
	protected void writeIntoFile(List<Document> newList,String fileHeader, File testRunFile){
		
		try {
			
			List<String> tmpList = new ArrayList<String>();
			
			for(Document d:newList){
				
				tmpList.add(d.getString("ClassName"));
			}
			
			FileUtils.writeLines(testRunFile, tmpList);
			FileUtils.writeStringToFile(testRunFile, fileHeader, true);
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	protected File createFile(){
		try {
			File testRunFile = new File(System.getProperty("user.home") + "/Desktop/CIDashBoardTemp/testRunData.txt");
			if (!testRunFile.exists()) {
				System.out.println("File dose not exists, nevermind creating a new file!");
				testRunFile.createNewFile();
			}
			
			return testRunFile;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

		
	}
	
	protected void clearScreen(){
		for(int i=0;i<10;i++){
			System.out.println("\n\n\n\n\n\n\n\n\n\n");
		}
	}

}
