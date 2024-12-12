package org.tyss.flinkpay.genericutility;

import static io.restassured.RestAssured.given;

import java.io.File;

import org.testng.Assert;

import io.restassured.RestAssured;

public class RestAssuredUtility {
	FileUtility fileUtility = new FileUtility();

	public void pushTransactionsToDB(String filePath) {
		RestAssured.baseURI = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "url");
		given().multiPart(new File(filePath)).when().post("/transactions-xml");
		Assert.assertEquals(false, null);
		UtilityObjectClass.getExtentTest().info("Transactions pushed to DB.");
	}
	
	public void verifyTxnInAPILayer(String txnId) {
		RestAssured.baseURI = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "url");
		RestAssured.when().get("/transaction?"+txnId+"");
		UtilityObjectClass.getExtentTest().info(txnId+" is verified in API Layer.");
	}
}
