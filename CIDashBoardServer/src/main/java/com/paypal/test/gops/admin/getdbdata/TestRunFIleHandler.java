package com.paypal.test.gops.admin.getdbdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.Document;

public class TestRunFIleHandler {

	protected void writeIntoFile(List<Document> newList, String fileHeader,
			File testRunFile, String suiteName) {

		try {
			Collections.sort(newList, new MyListComp());

			List<String> tmpList = new ArrayList<String>();

			String testTagname = newList.get(0).getString("TestTagName");
			tmpList.add("Suite Name Is: "+suiteName);
			tmpList.add("*************Test with Tag Name: " + testTagname
					+ "************");
			tmpList.add("\n");
			for (Document d : newList) {
				if (!testTagname.equals(d.get("TestTagName"))) {
					testTagname = d.get("TestTagName").toString();
					tmpList.add("\n");
					tmpList.add("*************Test with Tag Name: "
							+ testTagname + "************");
					tmpList.add("\n");
				}

				tmpList.add(d.getString("ClassName"));

			}

			FileUtils.writeLines(testRunFile, tmpList);
			FileUtils.writeStringToFile(testRunFile, fileHeader, true);

		} catch (NullPointerException e) {
			System.out
					.println("Looks Like Test Tag is not present for few test runs recorded!");
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected File createFile() {
		try {
			File testRunFile = new File(System.getProperty("user.home")
					+ "/Desktop/CIDashBoardTemp/testRunData.txt");
			if (!testRunFile.exists()) {
				System.out
						.println("File dose not exists, nevermind creating a new file!");
				testRunFile.createNewFile();
			}

			return testRunFile;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	protected void clearScreen() {
		for (int i = 0; i < 100; i++) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n");
		}
	}

}

class MyListComp implements Comparator<Document> {

	@Override
	public int compare(Document d1, Document d2) {

		return d1.get("TestTagName").toString()
				.compareTo(d2.get("TestTagName").toString());

	}
}
