package org.tyss.flinkpay.genericutility;

import org.testng.Assert;

import com.aventstack.extentreports.Status;

public class AssertionUtility {
	public void assertTwoStrings(String string1, String string2) {
		try {
			Assert.assertEquals(string1, string2);
			UtilityObjectClass.getExtentTest().log(Status.PASS, "Both the Strings are Same.");
		} catch (AssertionError e) {
			UtilityObjectClass.getExtentTest().log(Status.FAIL, "There is a differnce between both the images. -"+e.getMessage());
			throw e;
		}
	}

	public void assertBooleanValue(boolean value) {
		try {
			Assert.assertTrue(value);
			UtilityObjectClass.getExtentTest().log(Status.PASS, "Output result value is TRUE.");
		} catch (AssertionError e) {
			UtilityObjectClass.getExtentTest().log(Status.FAIL, "Output result value is FALSE. -"+e.getMessage());
			throw e;
		}
	}
}
