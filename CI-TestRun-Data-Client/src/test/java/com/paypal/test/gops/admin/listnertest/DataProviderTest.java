package com.paypal.test.gops.admin.listnertest;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;




public class DataProviderTest {

	@BeforeClass
	public void setUp() {	
		
		
	}


	


	@Test(dataProvider="usertypes")
	
	public void makeReversalRec(String name) throws Exception {
		System.out.println("Test executed: "+name);
		Assert.assertTrue(false);
		
	}
	
	
	@DataProvider(name="usertypes")
	public Object [][] dataprovider() {
		
		
		Object [][] data =  {{"Name1"},{"Name2"},{"Name3"}, {"Name4"}};
		return data;
	}



	@AfterClass(alwaysRun=true)
	public void tearDown(){
		System.out.println("Tear down");
	}
}
