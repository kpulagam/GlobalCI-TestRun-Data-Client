package com.paypal.test.gops.admin.cilistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;

public class TestRunDataCollectionHelper {

	/**
	 * @param args
	 */
	private HashMap<String, Integer> listOfMethods = null;

	protected synchronized void collectCIData(IInvokedMethod currentTestRun,
			ITestResult testResult, CITestRunData setdata, String testClassStatus,
			Document doc, MongoDAO databaseObject, Logger log)  {
		int f = 0;
		boolean hasTestCaseStarted = false;
		

		String tmpClassName = currentTestRun.getTestMethod().getRealClass()
				.getName();

		if (setdata == null) {
			throw new NullPointerException("The object is null");
		} else {

			if (!setdata.getHasTestRunCompleted()) {

				if (setdata.getAllClassMethodMap().isEmpty()) {
					listOfMethods = new HashMap<String, Integer>();
					log.debug("INFO: First method set object created");
				
					setdata.setClassName(tmpClassName);

					listOfMethods.put(currentTestRun.getTestMethod()
							.getMethodName(), testResult.getStatus());

					setdata.setAllClassMethodMap(setdata.getClassName(),
							listOfMethods);

				}

				else if (setdata.getClassName() == tmpClassName
						&& !setdata.getAllClassMethodMap().containsKey(
								tmpClassName)) {
					listOfMethods.put(currentTestRun.getTestMethod()
							.getMethodName(), testResult.getStatus());

					setdata.setAllClassMethodMap(tmpClassName, listOfMethods);
				}

				else if (setdata.getClassName() == tmpClassName
						&& setdata.getAllClassMethodMap().containsKey(
								tmpClassName)) {
					log.debug(f+": I am in Data provider");
					
					log.debug("Class Name: "+tmpClassName+" method Name: "+currentTestRun.getTestMethod().getConstructorOrMethod().getConstructor());
					log.debug("Class Name: "+tmpClassName+" method Name: "+currentTestRun.getTestMethod().getMethodName());
					log.debug("Class Name: "+tmpClassName+" method Name: "+currentTestRun.getTestMethod().getConstructorOrMethod().getName());
					log.debug("Class Name: "+tmpClassName+" method Name: "+currentTestRun.getTestMethod().isTest());
					
					listOfMethods = new HashMap<String, Integer>();
					listOfMethods = setdata.getAllClassMethodMap().get(
							tmpClassName);
					if(listOfMethods.containsKey(currentTestRun.getTestMethod().getMethodName())&&currentTestRun.getTestMethod().isTest()){
						listOfMethods.put(Math.random()+"_"+currentTestRun.getTestMethod()
								.getMethodName(), testResult.getStatus());
						
					}else{
						listOfMethods.put(currentTestRun.getTestMethod()
								.getMethodName(), testResult.getStatus());
					}
					

					setdata.setAllClassMethodMap(tmpClassName, listOfMethods);
					f++;

				}

				else if (setdata.getClassName() != tmpClassName
						&& setdata.getAllClassMethodMap().containsKey(
								tmpClassName)) {

					listOfMethods = new HashMap<String, Integer>();
					listOfMethods = setdata.getAllClassMethodMap().get(
							tmpClassName);
					listOfMethods.put(currentTestRun.getTestMethod()
							.getMethodName(), testResult.getStatus());

					setdata.setAllClassMethodMap(tmpClassName, listOfMethods);

				}

				else if (setdata.getClassName() != tmpClassName
						&& !setdata.getAllClassMethodMap().containsKey(
								tmpClassName)) {

					listOfMethods = new HashMap<String, Integer>();
					log.debug("INFO: New method set object created");
					setdata.setClassName(tmpClassName);
					listOfMethods.put(currentTestRun.getTestMethod()
							.getMethodName(), testResult.getStatus());

					setdata.setAllClassMethodMap(tmpClassName, listOfMethods);

				}

			}

		}

		for (String className : setdata.getAllClassMethodMap().keySet()) {

			int i = 1;
			int j = 0;
			for (Entry<String, Integer> methodName : setdata
					.getAllClassMethodMap().get(className).entrySet()) {
				j = j + methodName.getValue();

				if (j == 0) {
					testClassStatus = "Not Started";

				}

				else {

					testClassStatus = "In Progress";
					hasTestCaseStarted = true;
				}
				i = i * methodName.getValue();

			}

			if (hasTestCaseStarted) {
				if (i == 1)
					testClassStatus = "Passed";
				else if (i > 1)
					testClassStatus = "Failed";
			}

			if (setdata.getAllClassMethodMap().get(className).containsValue(3)) {
				testClassStatus = "Failed";

			}

			setdata.setTestClassResultSet(className, testClassStatus);

		}

		for (String className : setdata.getTestClassResultSet().keySet()) {

			doc = new Document("ClassName", className).append("Status",
					setdata.getTestClassResultSet().get(className)).append(
					"Methods", this.setMethodStatus(setdata, className));
			databaseObject.writeIntoDb(doc, className);

		}

	}

	private synchronized List<String> setMethodStatus(CITestRunData setdata,
			String className) {
		List<String> newList = new ArrayList<String>();

		for (Entry<String, Integer> methodName : setdata.getAllClassMethodMap()
				.get(className).entrySet()) {

			switch (methodName.getValue()) {

			case 0:
				newList.add("Method: " + methodName.getKey()
						+ " Status: Not Started");
				break;
			case 1:
				newList.add("Method: " + methodName.getKey()
						+ " Status: Passed");
				break;
			case 2:
				newList.add("Method: " + methodName.getKey()
						+ " Status: Failed");
				break;
			case 3:
				newList.add("Method: " + methodName.getKey()
						+ " Status: Skipped");
				break;
			default:
				newList.add("Method: " + methodName.getKey()
						+ " Status: Failed");

			}

		}
		return newList;

	}

}
