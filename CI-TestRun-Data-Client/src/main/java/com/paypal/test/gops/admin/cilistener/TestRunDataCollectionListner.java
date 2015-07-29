package com.paypal.test.gops.admin.cilistener;

import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.xml.XmlClass;

public class TestRunDataCollectionListner implements ITestListener, ISuiteListener,
		IInvokedMethodListener {

	static final Logger log = Logger.getLogger("testRunDataLogger");
	private CITestRunData setdata = null;

	private String testClassStatus = null;
	private TestRunDataCollectionHelper testRunDataCollectionHelper = new TestRunDataCollectionHelper();
	private Document doc = new Document();
	private MongoDAO databaseObject = null;

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		
		try {
			testRunDataCollectionHelper.collectCIData(method, testResult, setdata,
					testClassStatus, doc, databaseObject,log);
		} catch (Exception e) {

			log.error(
					"Error Encountered while colletiing data related to Method with name: "
							+ method.getTestMethod().getMethodName(), e);

		}

	}

	/**
	 * 
	 * Test Suite Related
	 * 
	 */

	@Override
	public void onStart(ISuite suite) {
		try {

			Date date = new Date();
			setdata = new CITestRunData();
			System.out.println("I am In started");

			setdata.setHasTestRunCompleted(false);
			setdata.setSuiteName(suite.getName());
			setdata.setTestRunStartTime(date.getTime());
			databaseObject = new MongoDAO(setdata.getSuiteName());

		} catch (Exception e) {

			log.fatal(
					"Error Encountered in initializing the CI report, No data will be collected ",
					e);

		}
	}

	@Override
	public void onFinish(ISuite suite) {
		Date date = new Date();
		setdata.setHasTestRunCompleted(true);
		databaseObject.completeTest(setdata.getSuiteName());
		log.debug("Info: Suite name is :" + setdata.getSuiteName());
		log.debug("Info: Test run start time is :"
				+ setdata.getTestRunStartTime());
		log.debug("Info: Test run end time is :" + date.toString());
		
	}

	/**
	 * 
	 * Test Tag Related
	 * 
	 */

	@Override
	public void onStart(ITestContext arg0) {
		
		for(XmlClass c :arg0.getCurrentXmlTest().getClasses()){
			setdata.setTestClassTagList(c.getName(),arg0.getName());
		}
		

	}

	@Override
	public void onFinish(ITestContext arg0) {

		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * Test Method Related
	 * 
	 */

	@Override
	public void onTestStart(ITestResult arg0) {

	}

	@Override
	public void onTestSuccess(ITestResult arg0) {

	}

	@Override
	public void onTestFailure(ITestResult arg0) {

	}

	@Override
	public void onTestSkipped(ITestResult arg0) {

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

}