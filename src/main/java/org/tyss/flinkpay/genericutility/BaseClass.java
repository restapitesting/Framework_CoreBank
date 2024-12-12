package org.tyss.flinkpay.genericutility;

import java.net.MalformedURLException;
import java.sql.Connection;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.tyss.flinkpay.objectrepository.HomePage;
import org.tyss.flinkpay.workflowutility.FlinkPayWorkflowsUtility;

import com.jcraft.jsch.Session;

public class BaseClass {
	public ExcelUtility excelUtility = new ExcelUtility();
	public FileUtility fileUtility = new FileUtility();
	public JavaUtility javaUtility = new JavaUtility();
	public AssertionUtility assertionUtility = new AssertionUtility();
	public RestAssuredUtility restAssuredUtility = new RestAssuredUtility();
	public WebDriverUtility webDriverUtility = new WebDriverUtility();
	public DatabaseUtility databaseUtilty = new DatabaseUtility();
	public SshUtility sshUtility = new SshUtility();
	public FlinkPayWorkflowsUtility workflowUtility = new FlinkPayWorkflowsUtility();
	public WebDriver driver;
	public Connection connection;
	public Session session;

	@BeforeSuite
	public void configBS() {
		System.out.println("*********Connect to the Database*********");
		String dbUrl = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "dbUrl");
		String dbUserName = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "dbUsername");
		String dbPassword = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "dbPassword");
		connection = databaseUtilty.connectDB(dbUrl, dbUserName, dbPassword);
		UtilityObjectClass.setDbConnection(connection);

		System.out.println("*********Connect to the Linux Server*********");
		String sshHost = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "sshHost");
		String sshPortNumber = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "sshPortNumber");
		String sshUsername = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "sshUsername");
		String sshPassword = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "sshPassword");

		session = sshUtility.connectToUnixServer(sshHost, sshPortNumber, sshUsername, sshPassword);
		UtilityObjectClass.setSshSession(session);
	}

	@BeforeClass
	public void configBC() throws MalformedURLException {
		System.out.println("*********Open Browser*********");
		String url = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "url");
		String browserName = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "browser");
	
		driver = webDriverUtility.openBrowserWindow(browserName);
		UtilityObjectClass.setDriver(driver);
		webDriverUtility.maximizeBrowserWindow();
		webDriverUtility.navigateToUrl(url);
		webDriverUtility.implicitWait(IConstants.IMPLICIT_WAIT_TIME);
	}

	@BeforeMethod
	public void configBM(ITestResult result) {
		System.out.println("*********SignIn to the Application*********");
		String username = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "adminUsername");
		String password = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "adminPassword");
		workflowUtility.loginToApplication(username, password);
	}
	
	@AfterMethod
	public void configAM(ITestResult result) {
		System.out.println("*********Sign out from the Application*********");
		HomePage homePage = new HomePage(driver);
		webDriverUtility.clickOnElement(homePage.getLogoutButton());
	}

	@AfterClass
	public void configAC() {
		System.out.println("*********Close browser*********");
		webDriverUtility.closeBrowser();
	}

	@AfterSuite
	public void configAS() {
		System.out.println("*********Disconnect from the Database*********");
		databaseUtilty.closeDB(connection);

		System.out.println("*********Disconnect from the Linux Server*********");
		sshUtility.disconnectFromUnixServer(session);
	}
	
	
}
