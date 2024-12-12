package org.tyss.flinkpay.genericutility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class JavaUtility {

	public String getDateAndTimeInSpecifiedFormat(String format) {
		return new SimpleDateFormat(format).format(new Date()).toString();
	}

	public int getRandomNumber(int range) {
		return new Random().nextInt(range);
	}

	public String getCurrentProjectDirectory() {
		return System.getProperty("user.dir");
	}

	// function to generate a random string of length n
	public String getRandomAlphaNumericString(int n) {
		// choose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {
			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());
			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}
	
	public String generateDateWithOffset(String offset, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(offset));
		return new SimpleDateFormat(format).format(calendar.getTime());
	}
}