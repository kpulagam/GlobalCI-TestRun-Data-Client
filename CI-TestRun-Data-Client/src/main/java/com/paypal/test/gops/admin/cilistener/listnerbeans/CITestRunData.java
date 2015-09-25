package com.paypal.test.gops.admin.cilistener.listnerbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CITestRunData {

	private boolean hasTestRunCompleted;
	private LinkedHashMap<String, HashMap<String, Integer>> allClassMethodMap = new LinkedHashMap<String, HashMap<String, Integer>>();
	private Map<String, String> testClassResultSet = new HashMap<String, String>();
	private Map<String, String> testClassTagList = new HashMap<String, String>();

	private String className;
	private String suiteName;
	private long testRunStartTime;
	private long testRunEndTime;
	private Map<String, ArrayList<String>> exceptionStackObjectMap = new HashMap<String, ArrayList<String>>();
	
	public Map<String, ArrayList<String>> getExceptionStackObjectMap() {
		return exceptionStackObjectMap;
	}

	public void setExceptionStackObjectMap(String className ,ArrayList<String> t) {
		
		exceptionStackObjectMap.put(className, t);
		
	}	

	public Map<String, String> getTestClassTagList() {
		return testClassTagList;
	}

	public void setTestClassTagList(String className, String testTagName) {
		this.testClassTagList.put(className, testTagName);
	}

	public LinkedHashMap<String, HashMap<String, Integer>> getAllClassMethodMap() {
		return allClassMethodMap;
	}

	public void setAllClassMethodMap(String className, HashMap<String, Integer> listOfMethods) {
		this.allClassMethodMap.put(className, listOfMethods);
	}

	public Map<String, String> getTestClassResultSet() {
		return testClassResultSet;
	}

	public void setTestClassResultSet(String className, String Result) {
		this.testClassResultSet.put(className, Result);
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean getHasTestRunCompleted() {

		return this.hasTestRunCompleted;
	}

	public void setHasTestRunCompleted(boolean isClassExecutinCompleted) {
		this.hasTestRunCompleted = isClassExecutinCompleted;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public long getTestRunStartTime() {
		return testRunStartTime;
	}

	public void setTestRunStartTime(long testRunStartTime) {
		this.testRunStartTime = testRunStartTime;
	}

	public long getTestRunEndTime() {
		return testRunEndTime;
	}

	public void setTestRunEndTime(long testRunEndTime) {
		this.testRunEndTime = testRunEndTime;
	}

}