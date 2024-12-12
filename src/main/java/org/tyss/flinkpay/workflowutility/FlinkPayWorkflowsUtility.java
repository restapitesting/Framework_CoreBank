package org.tyss.flinkpay.workflowutility;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.Listeners;
import org.tyss.flinkpay.genericutility.DatabaseUtility;
import org.tyss.flinkpay.genericutility.ExcelUtility;
import org.tyss.flinkpay.genericutility.FileUtility;
import org.tyss.flinkpay.genericutility.IConstants;
import org.tyss.flinkpay.genericutility.JavaUtility;
import org.tyss.flinkpay.genericutility.RestAssuredUtility;
import org.tyss.flinkpay.genericutility.SshUtility;
import org.tyss.flinkpay.genericutility.UtilityObjectClass;
import org.tyss.flinkpay.genericutility.WebDriverUtility;
import org.tyss.flinkpay.objectrepository.AmountTransferPage;
import org.tyss.flinkpay.objectrepository.HomePage;
import org.tyss.flinkpay.objectrepository.InboundPage;
import org.tyss.flinkpay.objectrepository.OutboundPage;
import org.tyss.flinkpay.objectrepository.WelcomePage;

import com.jayway.jsonpath.JsonPath;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@Listeners(org.tyss.flinkpay.genericutility.ListenerImplementationClass.class)
public class FlinkPayWorkflowsUtility {
	WebDriverUtility webDriverUtility = new WebDriverUtility();
	ExcelUtility excelUtility = new ExcelUtility();
	FileUtility fileUtility = new FileUtility();
	DatabaseUtility databaseUtility = new DatabaseUtility();
	RestAssuredUtility restAssuredUtility = new RestAssuredUtility();
	SshUtility sshUtility = new SshUtility();
	JavaUtility javaUtility = new JavaUtility();

	public void loginToApplication(String username, String password) {
		WelcomePage welcomePage = new WelcomePage(UtilityObjectClass.getDriver());
		webDriverUtility.enterInputIntoElement(welcomePage.getUsernameTextfield(), username);
		webDriverUtility.enterInputIntoElement(welcomePage.getPasswordTextfield(), password);
		webDriverUtility.clickOnElement(welcomePage.getLogInButton());
	}

	public void loginToAppAsBankB() {
		String username = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "bankUsername");
		String password = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "bankPassword");
		loginToApplication(username, password);
		UtilityObjectClass.getExtentTest().pass("Logged in as Bank with UN: "+username+" and PWD: "+password);
	}

	public String json_Txns_Creation_Simulation_Tool(String sheetName) {
		excelUtility.generateAndWriteDataToExcelForJson(IConstants.JSON_EXCEL_DATA_FILEPATH, "Txn_Info");
		List<Map<String, String>> entireData = excelUtility.getDataFromExcel(IConstants.JSON_EXCEL_DATA_FILEPATH, sheetName);
		String jsonStaticData = fileUtility.readDataFromTextFile(IConstants.JSON_SCHEMA_FILEPATH);
		String copyStaticData = jsonStaticData;

		String jsonArrayAsString = "[";
		for (Map<String, String> map : entireData) {
			for (Entry<String, String> singleRowData : map.entrySet()) {
				jsonStaticData = jsonStaticData.replaceAll(singleRowData.getKey().trim(), singleRowData.getValue().trim());
			}
			jsonArrayAsString = jsonArrayAsString+jsonStaticData.trim()+",";
			jsonStaticData = copyStaticData;
		}

		// Get jsons file path
		String jsonFilePath = fileUtility.writeDataToTxtFile(IConstants.GENERATED_JSON_FILEPATH, "Json_txns_"+javaUtility.getDateAndTimeInSpecifiedFormat("yyyy_MM_dd_hh_mm_ss"),jsonArrayAsString.substring(0,jsonArrayAsString.length()-1)+"]");
		UtilityObjectClass.getExtentTest().info("Json file Generated."+jsonFilePath);
		return jsonFilePath;
	}


	public void initiateJsonTxns(String jsonFilePath) {
		// Get Remote Directory
		String remoteFolderDirectory = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "jsonFolderPath");

		// Transfer Json file from local to unix server
		String remoteFilePath = sshUtility.ftpFromLocalToRemote(UtilityObjectClass.getSshSession(), jsonFilePath, remoteFolderDirectory);

		// Push Transactions to Kafka by running shell script
		String shellScriptPath = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "jsonShFilePath");
		String command = shellScriptPath+" /"+remoteFilePath;
		sshUtility.executeUnixCommand(UtilityObjectClass.getSshSession(), command);
	}

	public List<String> SMART_SWIFT_TRXN_Creation_Simulation_tool(String sheetName) {
		List<String> pathsList = new LinkedList<String>();

		excelUtility.generateAndWriteDataToExcelForSwift(IConstants.SWIFT_EXCEL_DATA_FILEPATH, sheetName);
		List<Map<String, String>> entireData = excelUtility.getDataFromExcel(IConstants.SWIFT_EXCEL_DATA_FILEPATH, sheetName);
		String xmlStaticData = fileUtility.readDataFromTextFile(IConstants.SWIFT_SCHEMA_FILEPATH);
		String copyStaticData = xmlStaticData;

		File folder = new File(IConstants.GENERATED_SWIFT_MESSAGEPATH+javaUtility.getDateAndTimeInSpecifiedFormat("yyyy_MM_dd_hh_mm_ss"));
		folder.mkdir();

		String finalString = "";

		for (Map<String, String> map : entireData) {
			for (Entry<String, String> singleRowData : map.entrySet()) {
				xmlStaticData = xmlStaticData.replaceAll(singleRowData.getKey().trim(), singleRowData.getValue().trim());
			}
			finalString ="	<Transaction>\n"+xmlStaticData+"	</Transaction>\n";
			xmlStaticData = copyStaticData;
			String txtFilePath = fileUtility.writeDataToTxtFile(folder.getAbsolutePath(), map.get("TXN_REF_ID_VAR"),"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Transactions>\n"+finalString+"</Transactions>");
			pathsList.add(txtFilePath);
		}
		return pathsList;
	}

	public void verifyTxnInAll3Layers() {
		List<String> txnList = excelUtility.generateAndWriteDataToExcelForJson(IConstants.JSON_EXCEL_DATA_FILEPATH, "Txn_Info");
		webDriverUtility.refreshBrowserWindow();
		webDriverUtility.waitForSeconds(1);
		for (String txnId : txnList) {
			UtilityObjectClass.setExtentTest(UtilityObjectClass.getExtentReports().createTest(txnId));

			// Verification in GUI Layer
			HomePage homepage = new HomePage(UtilityObjectClass.getDriver());
			webDriverUtility.clickOnElement(homepage.getAllTransactionsLink());
			webDriverUtility.waitForMillis(500);
			webDriverUtility.clearAndEnterInputIntoElement(txnId, homepage.getSearchTextField());
			webDriverUtility.verifyElementIsDisplayed(homepage.getTransactionId(txnId));

			// Verification in DB Layer
			databaseUtility.verifyColumnContainsData(UtilityObjectClass.getDbConnection(), "transaction", "transaction_id", txnId);

			// Verification in API Layer
			restAssuredUtility.verifyTxnInAPILayer(txnId);
		}
	}

	public String transferAmount(String senderAccountNumber, String senderAccountName, String chargesCode, String beneficiaryAccountNumber, String beneficiaryAccountName, String transferAmount, String mobileNumber, String bankBranch, String bankName) {

		AmountTransferPage amountTransferPage = new AmountTransferPage(UtilityObjectClass.getDriver());
		webDriverUtility.enterInputIntoElement(amountTransferPage.getSenderAccountNumberTextfield(), senderAccountNumber);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getSenderAccountNameTextfield(), senderAccountName);
		webDriverUtility.selectByVisibleText(amountTransferPage.getChargesCodeDropdown(), chargesCode);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiaryAccountNumberTextfield(), beneficiaryAccountNumber);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiaryAccountNameTextfield(), beneficiaryAccountName);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getTransferAmountTextfield(), transferAmount);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiaryMobileNumberTextfield(), mobileNumber);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiarybankBranchTextfield(), bankBranch);
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiarybankNameTextfield(), bankName);
		webDriverUtility.clickOnElement(amountTransferPage.getTransferButton());
		webDriverUtility.waitForSeconds(1);

		String refId = webDriverUtility.getTextfromAlertAndClose().split("is")[1].trim();
		UtilityObjectClass.getExtentTest().pass("Transfered amount and Ref Id is: "+refId);

		return refId;
	}

	public String transferAmount(String fileName, String sheetName, String tcId) {
		HomePage homePage = new HomePage(UtilityObjectClass.getDriver());
		Map<String, String> tcDataMap = excelUtility.getEntireTcDataBasedOnTcId("."+File.separator+fileName, sheetName, tcId);
		AmountTransferPage amountTransferPage = new AmountTransferPage(UtilityObjectClass.getDriver());
		webDriverUtility.enterInputIntoElement(amountTransferPage.getSenderAccountNumberTextfield(), tcDataMap.get("Sender_Account_Number"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getSenderAccountNameTextfield(), tcDataMap.get("Sender_Account_Name"));
		webDriverUtility.selectByVisibleText(amountTransferPage.getChargesCodeDropdown(), tcDataMap.get("Charges_Code"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiaryAccountNumberTextfield(), tcDataMap.get("Benificiary_Account_Number"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiaryAccountNameTextfield(), tcDataMap.get("Benificiary_Account_Name"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getTransferAmountTextfield(), tcDataMap.get("Transfer_Amount"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiaryMobileNumberTextfield(), tcDataMap.get("Mobile_Number"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiarybankBranchTextfield(), tcDataMap.get("Bank_Branch"));
		webDriverUtility.enterInputIntoElement(amountTransferPage.getBeneficiarybankNameTextfield(), tcDataMap.get("Bank_Name"));
		webDriverUtility.clickOnElement(amountTransferPage.getTransferButton());
		webDriverUtility.waitForSeconds(1);
		String refId = webDriverUtility.getTextfromAlertAndClose().split("is")[1].trim();
		
		UtilityObjectClass.getExtentTest().pass("Transfered amount and Ref Id is: "+refId);
		
		// Logout as admin
		webDriverUtility.clickOnElement(homePage.getLogoutButton());
		return refId;
	}
	
	public List<String> transferAmountToMultipleAccounts(String fileName, String sheetName) {
		List<String> refIdList = new LinkedList<String>();
		List<Map<String, String>> outboundDataList = excelUtility.getDataFromExcel("."+File.separator+fileName, sheetName);
		
		// Create object for POM Pages
		HomePage homePage = new HomePage(UtilityObjectClass.getDriver());
		
		for (Map<String, String> outboundData : outboundDataList) {
			// Click on amount transfer link
			webDriverUtility.clickOnElement(homePage.getAmountTransferLink());

			String refId = transferAmount(outboundData.get("Sender_Account_Number"), outboundData.get("Sender_Account_Name"), outboundData.get("Charges_Code"), outboundData.get("Benificiary_Account_Number"), outboundData.get("Benificiary_Account_Name"), outboundData.get("Transfer_Amount"), outboundData.get("Mobile_Number"), outboundData.get("Bank_Branch"), outboundData.get("Bank_Name"));
			refIdList.add(refId);
		}
		
		return refIdList;
	}

	public void initiateTrxns(List<String> filePaths) {
		// Get Remote folder path from property file
		String remoteFolderDirectory = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "swiftFolderPath");

		for (int i = 0; i < filePaths.size(); i++) {
			// File transfer from local to remote
			String remoteFilePath = sshUtility.ftpFromLocalToRemote(UtilityObjectClass.getSshSession(), filePaths.get(i), remoteFolderDirectory);

			// Get Shell script path from property file
			String shellScriptPath = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "swiftShFilePath");

			// Create command to push Txns to Kafka
			String command = shellScriptPath+" /"+remoteFilePath;

			// Push Txns to Kafka by running shell script
			sshUtility.executeUnixCommand(UtilityObjectClass.getSshSession(), command);

			//			restAssuredUtility.pushTransactionsToDB(filePaths.get(i));
		}
	}

	public void verifySwiftMessageTxnsBulk(String fileName, String sheetName, List<String> refIdList) {
		OutboundPage outboundPage = new OutboundPage(UtilityObjectClass.getDriver());
		HomePage homePage = new HomePage(UtilityObjectClass.getDriver());
		webDriverUtility.clickOnElement(homePage.getOutBoundsLink());

		int index = 0;
		for (String refId : refIdList) {
			String status = webDriverUtility.getTextFromElement(outboundPage.getStatusText(refId));
			UtilityObjectClass.getExtentTest().info("Reference Id: "+refId+" status is: "+status );
			boolean isVerified = webDriverUtility.verifyElementIsDisplayed(outboundPage.getReferenceId(refId));
			if (isVerified) {
				UtilityObjectClass.getExtentTest().pass(refId+" is verified in GUI Layer.");
			} else {
				UtilityObjectClass.getExtentTest().fail(refId+" is NOT available in GUI Layer.");
			}
			webDriverUtility.clickOnElement(outboundPage.getDownloadIcon(refId));
			String downloadedFilePath = fileUtility.getDownloadedFilePath(10);
			String fileData = fileUtility.readDataFromTextFile(downloadedFilePath);
			UtilityObjectClass.getExtentTest().info("SWIFT Message: "+fileData);
			List<String> txnDataList = excelUtility.getTxnDataFromExcelBasedOnIndex(fileName, sheetName, index);

			for (String data : txnDataList) {
				if (fileData.contains(data)) {
					UtilityObjectClass.getExtentTest().info(data+" is present in swift file.");
				} else {
					UtilityObjectClass.getExtentTest().info(data+" is NOT present in swift file.");
				}
			}
			index++;
		}
	}

	public void verifySwiftTxnsInGUI(List<String> txnList) {
		InboundPage inboundPage = new InboundPage(UtilityObjectClass.getDriver());
		HomePage homepage = new HomePage(UtilityObjectClass.getDriver());
		webDriverUtility.refreshBrowserWindow();
		webDriverUtility.waitForSeconds(1);
		for (String txnId : txnList) {
			UtilityObjectClass.setExtentTest(UtilityObjectClass.getExtentReports().createTest(txnId));
			UtilityObjectClass.put(txnId, UtilityObjectClass.getExtentTest());

			// Verification in GUI Layer
			webDriverUtility.clickOnElement(homepage.getInBoundsLink());
			webDriverUtility.waitForMillis(500);
			webDriverUtility.clearAndEnterInputIntoElement(txnId, inboundPage.getSearchTextField());
			boolean isVerified = webDriverUtility.verifyElementIsDisplayed(inboundPage.getReferenceId(txnId));
			if (isVerified) {
				UtilityObjectClass.getExtentTest().pass(txnId+" is verified in GUI Layer.");
			} else {
				UtilityObjectClass.getExtentTest().fail(txnId+" is NOT available in GUI Layer.");
			}
		}
	}

	public void verifySwiftTxnsInDatabase(List<String> txnList) {
		for (String txnId : txnList) {
			UtilityObjectClass.setExtentTest(UtilityObjectClass.get(txnId));
			
			boolean isVerified = databaseUtility.verifyColumnContainsData(UtilityObjectClass.getDbConnection(), "inbound_transactions", "reference_number", txnId);
			if (isVerified) {
				UtilityObjectClass.getExtentTest().pass(txnId+" is verified in DB Layer.");
			} else {
				UtilityObjectClass.getExtentTest().fail(txnId+" is NOT available in DB.");
			}
		}
	}

	public void verifySwiftTxnsInAPILayer(List<String> txnList) {
		RestAssured.baseURI = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "url");
		Response response = RestAssured.when().get("/inbound-transaction/all-inbounds/rmgyantra");
		
		List<String> txnListResponse = JsonPath.read(response.asString(), "$.[*].referenceNumber");
		for (String txnId : txnList) {
			UtilityObjectClass.setExtentTest(UtilityObjectClass.get(txnId));
		
			int index = txnListResponse.indexOf(txnId);
			
			UtilityObjectClass.getExtentTest().info("Response Body: "+JsonPath.read(response.asString(), "$.["+index+"]").toString());

			boolean isVerified = txnListResponse.contains(txnId);
			if (isVerified) {
				UtilityObjectClass.getExtentTest().pass(txnId+" is verified in API Layer.");
			} else {
				UtilityObjectClass.getExtentTest().fail(txnId+" is NOT available in API Layer.");
			}
		}
	}

	public void verifySwiftTxnAsBank(String refId) {
		HomePage homePage = new HomePage(UtilityObjectClass.getDriver());
		InboundPage inboundPage = new InboundPage(UtilityObjectClass.getDriver());

		// Login as Bank
		loginToAppAsBankB();
		webDriverUtility.clickOnElement(homePage.getInBoundsLink());
		webDriverUtility.enterInputIntoElement(inboundPage.getSearchTextField(), refId);
		webDriverUtility.verifyElementIsDisplayed(inboundPage.getReferenceId(refId));

		UtilityObjectClass.getExtentTest().pass("Verified Txn as Bank.");
	}
}

