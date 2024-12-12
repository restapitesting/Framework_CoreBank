package org.tyss.flinkpay.objectrepository;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AmountTransferPage {
	WebDriver driver;
	@FindBy(name = "senderAccountNumber") private WebElement senderAccountNumberTextfield;
	@FindBy(name = "senderAccountName") private WebElement senderAccountNameTextfield;
	@FindBy(name = "chargesCode") private WebElement chargesCodeDropdown;
	@FindBy(name = "beneficiaryCreditAccountNumber") private WebElement beneficiaryAccountNumberTextfield;
	@FindBy(name = "beneficiaryAccountName") private WebElement beneficiaryAccountNameTextfield;
	@FindBy(name = "transferAmount") private WebElement transferAmountTextfield;
	@FindBy(name = "beneficiaryMobileNumber") private WebElement beneficiaryMobileNumberTextfield;
	@FindBy(name = "bankBranch") private WebElement beneficiarybankBranchTextfield;
	@FindBy(name = "bankName") private WebElement beneficiarybankNameTextfield;
	@FindBy(xpath = "//button[@type='submit']") private WebElement transferButton;

	public AmountTransferPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public WebElement getSenderAccountNumberTextfield() {
		return senderAccountNumberTextfield;
	}

	public void setSenderAccountNumberTextfield(WebElement senderAccountNumberTextfield) {
		this.senderAccountNumberTextfield = senderAccountNumberTextfield;
	}

	public WebElement getSenderAccountNameTextfield() {
		return senderAccountNameTextfield;
	}

	public void setSenderAccountNameTextfield(WebElement senderAccountNameTextfield) {
		this.senderAccountNameTextfield = senderAccountNameTextfield;
	}

	public WebElement getChargesCodeDropdown() {
		return chargesCodeDropdown;
	}

	public void setChargesCodeDropdown(WebElement chargesCodeDropdown) {
		this.chargesCodeDropdown = chargesCodeDropdown;
	}

	public WebElement getBeneficiaryAccountNumberTextfield() {
		return beneficiaryAccountNumberTextfield;
	}

	public void setBeneficiaryAccountNumberTextfield(WebElement beneficiaryAccountNumberTextfield) {
		this.beneficiaryAccountNumberTextfield = beneficiaryAccountNumberTextfield;
	}

	public WebElement getBeneficiaryAccountNameTextfield() {
		return beneficiaryAccountNameTextfield;
	}

	public void setBeneficiaryAccountNameTextfield(WebElement beneficiaryAccountNameTextfield) {
		this.beneficiaryAccountNameTextfield = beneficiaryAccountNameTextfield;
	}

	public WebElement getTransferAmountTextfield() {
		return transferAmountTextfield;
	}

	public void setTransferAmountTextfield(WebElement transferAmountTextfield) {
		this.transferAmountTextfield = transferAmountTextfield;
	}

	public WebElement getBeneficiaryMobileNumberTextfield() {
		return beneficiaryMobileNumberTextfield;
	}

	public void setBeneficiaryMobileNumberTextfield(WebElement beneficiaryMobileNumberTextfield) {
		this.beneficiaryMobileNumberTextfield = beneficiaryMobileNumberTextfield;
	}

	public WebElement getBeneficiarybankBranchTextfield() {
		return beneficiarybankBranchTextfield;
	}

	public void setBeneficiarybankBranchTextfield(WebElement beneficiarybankBranchTextfield) {
		this.beneficiarybankBranchTextfield = beneficiarybankBranchTextfield;
	}

	public WebElement getBeneficiarybankNameTextfield() {
		return beneficiarybankNameTextfield;
	}

	public void setBeneficiarybankNameTextfield(WebElement beneficiarybankNameTextfield) {
		this.beneficiarybankNameTextfield = beneficiarybankNameTextfield;
	}

	public WebElement getTransferButton() {
		return transferButton;
	}

	public void setTransferButton(WebElement transferButton) {
		this.transferButton = transferButton;
	}

	
}
