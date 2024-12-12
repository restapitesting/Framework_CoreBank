package org.tyss.flinkpay.objectrepository;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OutboundPage {
	WebDriver driver;
	@FindBy(xpath = "//input[@placeholder='Search by Reference Id']") private WebElement searchTextField;

	public OutboundPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public WebElement getSearchTextField() {
		return searchTextField;
	}

	public void setSearchTextField(WebElement searchTextField) {
		this.searchTextField = searchTextField;
	}

	public WebElement getStatusText(String refId) {
		return driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+refId+"']/following-sibling::td[6]"));
	}

	public WebElement getDownloadIcon(String refId) {
		return driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+refId+"']/following-sibling::td[7]/*[name()='svg']"));
	}
	
	public WebElement getReferenceId(String refId) {
		return driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+refId+"']"));
	}
	
}
