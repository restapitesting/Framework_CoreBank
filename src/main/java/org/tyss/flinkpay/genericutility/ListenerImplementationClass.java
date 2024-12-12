package org.tyss.flinkpay.genericutility;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
public class ListenerImplementationClass  extends BaseClass implements ITestListener {

	@Override
	public void onStart(ITestContext context) {
		JavaUtility javaUtility = new JavaUtility();
		FileUtility fileUtility = new FileUtility();
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(javaUtility.getCurrentProjectDirectory()+"\\Execution_Reports\\"+"MyReport"+"_"+javaUtility.getDateAndTimeInSpecifiedFormat("yyyyMMdd_HHmmss")+".html");
		sparkReporter.config().setDocumentTitle("Test Report");
		sparkReporter.config().setReportName("SimulationProject Automation Suite Report");
		sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);
		
		ExtentReports extentReports = new ExtentReports();
		extentReports.attachReporter(sparkReporter);
		extentReports.setSystemInfo("Customer Name", "Mashreq Bank");
		extentReports.setSystemInfo("Environment", "Production");
		String browserName = fileUtility.getDataFromPropertyFile(IConstants.COMMON_DATA_FILEPATH, "browser");
		extentReports.setSystemInfo("Browser Name", browserName);
		extentReports.setSystemInfo("User", "Channa S");
		UtilityObjectClass.setExtentReports(extentReports);
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest extentTestReport = UtilityObjectClass.getExtentReports().createTest(convertToTitleCase(result.getMethod().getMethodName()));
		UtilityObjectClass.setExtentTest(extentTestReport);
		UtilityObjectClass.getExtentTest().assignAuthor("Channa S");
		UtilityObjectClass.getExtentTest().assignCategory("Generic Test Scripts");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		UtilityObjectClass.getExtentTest().log(Status.PASS, "Test Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		UtilityObjectClass.getExtentTest().log(Status.FAIL, "Test Failed: " + result.getThrowable());
		String screenshotPath = getScreenshotAsBase64(UtilityObjectClass.getDriver());
		UtilityObjectClass.getExtentTest().addScreenCaptureFromBase64String(screenshotPath);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		UtilityObjectClass.getExtentTest().log(Status.SKIP, "Test Skipped");
	}

	@Override
	public void onFinish(ITestContext context) {
		UtilityObjectClass.getExtentReports().flush();
		if (driver != null) {
			driver.quit();
		}
	}

	public static String getScreenshotAsBase64(WebDriver driver)
	{
		TakesScreenshot takeScreenshot = (TakesScreenshot) driver;
		String path= takeScreenshot.getScreenshotAs(OutputType.BASE64);
		return path;
	}

	public static String convertToTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c : input.toCharArray()) {
			if (Character.isUpperCase(c)) {
				titleCase.append(" ");
			}
			if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			} else {
				c = Character.toLowerCase(c);
			}

			titleCase.append(c);
		}
		return titleCase.toString().trim();
	}
}
