package com.paypal.test.gops.admin.cilistener.listner;

import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.testng.IConfigurationListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.xml.XmlClass;

import com.mongodb.MongoException;
import com.paypal.test.gops.admin.cilistener.databasebeans.MongoListnerDataDAO;
import com.paypal.test.gops.admin.cilistener.listnerbeans.CITestRunData;

public class TestRunDataCollectionListner
		implements ITestListener, ISuiteListener, IInvokedMethodListener, IConfigurationListener {

	static final Logger log = Logger.getLogger("testRunDataLogger");
	private CITestRunData setdata = null;

	private String testClassStatus = null;
	private TestRunDataCollectionHelper testRunDataCollectionHelper = new TestRunDataCollectionHelper();
	private Document doc = new Document();
	private MongoListnerDataDAO databaseObject = null;

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

		try {
			testRunDataCollectionHelper.collectCIData(method, testResult, setdata, testClassStatus, doc, databaseObject,
					log);
		} catch (Exception e) {

			log.error("Error Encountered while colletiing data related to Method with name: "
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
			log.debug("Started collecting test run data");

			setdata.setHasTestRunCompleted(false);
			setdata.setSuiteName(suite.getName());
			setdata.setTestRunStartTime(date.getTime());
			databaseObject = new MongoListnerDataDAO(setdata.getSuiteName());

		} catch (Exception e) {

			log.fatal("Error Encountered in initializing the CI report, No data will be collected ", e);

		}
	}

	@Override
	public void onFinish(ISuite suite) {

		try {
			Date date = new Date();
			setdata.setHasTestRunCompleted(true);
			databaseObject.completeTest(setdata.getSuiteName());
			log.debug("Info: Suite name is :" + setdata.getSuiteName());
			log.debug("Info: Test run start time is :" + setdata.getTestRunStartTime());
			log.debug("Info: Test run end time is :" + date.toString());
			databaseObject.client.close();
			log.debug("DB connection closed");
			// testRunDataCollectionHelper.test(ciExceptions);

		} catch (MongoException e) {
			log.fatal("Error Occured with Data Base ");
			log.fatal(e);

		} catch (Exception e) {
			log.fatal("Some Error Occured with Data Base ");
			log.fatal(e);
		}

	}

	/**
	 * 
	 * Test Tag Related
	 * 
	 */

	@Override
	public void onStart(ITestContext arg0) {

		try {
			for (XmlClass c : arg0.getCurrentXmlTest().getClasses()) {
				setdata.setTestClassTagList(c.getName(), arg0.getName());
			}

		} catch (Exception e) {
			log.debug("Error occured while creating test tag and class list");
			log.fatal(e);

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
	public void onTestFailure(ITestResult testResult) {
		String className = testResult.getTestClass().getName().trim();		
		testRunDataCollectionHelper.recordException(setdata, className, testResult,log);

	}

	@Override
	public void onTestSkipped(ITestResult arg0) {

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}

	@Override
	public void onConfigurationFailure(ITestResult testResult) {

		String className = testResult.getTestClass().getName().trim();
		testRunDataCollectionHelper.recordException(setdata, className, testResult,log);

	}

	@Override
	public void onConfigurationSkip(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConfigurationSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

}