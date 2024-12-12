package org.tyss.flinkpay.genericutility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverUtility {
	JavaUtility javaUtility = new JavaUtility();

	public WebDriver openBrowserWindow(String browserName) throws MalformedURLException {
		RemoteWebDriver driver = null;
		if (browserName.trim().equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions option = new ChromeOptions();
			 driver = new RemoteWebDriver(new URL("http://localhost:5555"),option);
			//driver = new ChromeDriver();
		} else if(browserName.trim().equalsIgnoreCase("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
		} else if(browserName.trim().equalsIgnoreCase("edge")){
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
		}
		return driver;
	}

	public void navigateToUrl(String url) {
		UtilityObjectClass.getDriver().get(url);	
	}

	public void maximizeBrowserWindow() {
		UtilityObjectClass.getDriver().manage().window().maximize();
	}

	public void refreshBrowserWindow() {
		UtilityObjectClass.getDriver().navigate().refresh();
	}

	public void implicitWait(int seconds) {
		UtilityObjectClass.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
	}

	public void enterInputIntoElement(WebElement element, String input) {
		element.sendKeys(input);
	}

	public void clearAndEnterInputIntoElement(String input, WebElement element) {
		element.clear();
		element.sendKeys(input);
	}

	public void clickOnElement(WebElement element) {
		element.click();
	}
	
	public String getTextFromElement(WebElement element) {
		return element.getText();
	}
	
	public void selectByVisibleText(WebElement element, String visibleText) {
		Select select = new Select(element);
		select.selectByVisibleText(visibleText);
	}
	
	public void waitForSeconds(int seconds) {
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void waitForMillis(int milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean verifyElementIsDisplayed(WebElement element) {
		return element.isDisplayed();
	}
	
	public String getTextfromAlertAndClose() {
		String text = UtilityObjectClass.getDriver().switchTo().alert().getText();
		UtilityObjectClass.getDriver().switchTo().alert().accept();
		return text;
	}

	public String getScreenshotOfCurrentPage(String fileName, String directory) {
		// Ensure the directory exists
		try {
			Files.createDirectories(Paths.get(directory));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Take the screenshot
		File screenshot = ((TakesScreenshot) UtilityObjectClass.getDriver()).getScreenshotAs(OutputType.FILE);

		// Define the destination file
		File destinationFile = new File(directory, fileName + "_" + javaUtility.getDateAndTimeInSpecifiedFormat("yyyyMMdd_HHmmss") + ".png");

		// Copy the screenshot to the destination
		try {
			Files.copy(screenshot.toPath(), destinationFile.toPath());
			UtilityObjectClass.getExtentTest().info("Screenshot saved to: " + destinationFile.getAbsolutePath());
			System.out.println("Screenshot saved to: " + destinationFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destinationFile.getAbsolutePath();
	}

	public String getTextFromListOfWebElementsInString(List<WebElement> element) {
		String text = "";
		for (WebElement webElement : element) {
			text = text + webElement.getText()+" ";
		}
		return text.trim();
	}

	public void closeBrowser() {
		if (UtilityObjectClass.getDriver() != null) {
			UtilityObjectClass.getDriver().quit();
		}
	}
}
