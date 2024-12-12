package org.tyss.flinkpay.objectrepository;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
	WebDriver driver;
	@FindBy(xpath = "//div[normalize-space(text())='Logout']") private WebElement logoutButton;
	@FindBy(xpath = "//a[normalize-space(text())='All Transactions']") private WebElement allTransactionsLink;
	@FindBy(xpath = "//a[normalize-space(text())='Amount Transfer']") private WebElement amountTransferLink;
	@FindBy(xpath = "//a[normalize-space(text())='Outbounds']") private WebElement outBoundsLink;
	@FindBy(xpath = "//a[normalize-space(text())='Inbounds']") private WebElement inBoundsLink;
	@FindBy(xpath = "//input[@placeholder='Search by Transaction Id']") private WebElement searchTextField;

	public HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public WebElement getTransactionId(String transactionId) {
		return driver.findElement(By.xpath("//th[text()='TransactionId']/ancestor::table/descendant::td[text()='"+transactionId+"']"));
	}

	public WebElement getLogoutButton() {
		return logoutButton;
	}

	public void setLogoutButton(WebElement closeIcon) {
		this.logoutButton = closeIcon;
	}

	public WebElement getAllTransactionsLink() {
		return allTransactionsLink;
	}

	public void setAllTransactionsLink(WebElement allTransactionsLink) {
		this.allTransactionsLink = allTransactionsLink;
	}

	public WebElement getSearchTextField() {
		return searchTextField;
	}

	public void setSearchTextField(WebElement searchTextField) {
		this.searchTextField = searchTextField;
	}
	
	public WebElement getAmountTransferLink() {
		return amountTransferLink;
	}

	public void setAmountTransferLink(WebElement amountTransferLink) {
		this.amountTransferLink = amountTransferLink;
	}

	public WebElement getOutBoundsLink() {
		return outBoundsLink;
	}

	public void setOutBoundsLink(WebElement outBoundsLink) {
		this.outBoundsLink = outBoundsLink;
	}

	public WebElement getInBoundsLink() {
		return inBoundsLink;
	}

	public void setInBoundsLink(WebElement inBoundsLink) {
		this.inBoundsLink = inBoundsLink;
	}
	
}
