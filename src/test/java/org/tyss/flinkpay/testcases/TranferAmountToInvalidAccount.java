package org.tyss.flinkpay.testcases;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.tyss.flinkpay.genericutility.BaseClass;
import org.tyss.flinkpay.objectrepository.HomePage;

@Listeners(org.tyss.flinkpay.genericutility.ListenerImplementationClass.class)
public class TranferAmountToInvalidAccount extends BaseClass {

	@Test
	public void tranferAmountToInvalidAccount() {
		// Create objects for POM Pages
		HomePage homePage = new HomePage(driver);
		
		// step 1 : login & go to "Amount Trxn page" 
		webDriverUtility.clickOnElement(homePage.getAmountTransferLink());
		
		// step 2 :  Login as user-1 & send an mount 
		String refId = workflowUtility.transferAmount("Outbound_Data.xlsx", "Test_Data", "TC002");


		
	}
}
