package org.tyss.flinkpay.genericutility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 
 * This class contains the methods to Interact with property file and text files
 *
 */

public class FileUtility {

	JavaUtility javaUtility = new JavaUtility();

	/**
	 * This method is used to fetch the data from Property File
	 * @param Path
	 * @param key
	 * @return
	 */
	public String getDataFromPropertyFile(String filePath, String key) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties p = new Properties();
		try {
			p.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String value = p.getProperty(key).trim();
		return value;
	}

	public String readDataFromTextFile(String txtFilePath) {
		StringBuilder content = new StringBuilder();
		try {
			InputStream inputStream = new FileInputStream(txtFilePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			if (reader != null) {
				reader.close();
			}
		}catch (Exception e) {
		}
		return content.toString();
	}

	public String writeDataToTxtFile(String directoryPath, String fileName, String data) {
		BufferedWriter writer = null;
		String path = "";

		// Create the directory if it doesn't exist
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs(); // creates parent directories if necessary
		}

		// Create the file in the specified directory
		File file = new File(directory, fileName+".txt");

		try {
			path =file.getCanonicalPath();
			// Write data to the file
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(data);
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}


	public String getDownloadedFilePath(Integer maxWaitTime) {
		String userHome = System.getProperty("user.home"); 
		String downloadDirectory = Paths.get(userHome, "Downloads").toString();
		UtilityObjectClass.getExtentTest().info("File download directory is : "+downloadDirectory);

		// Your program element business logic goes here ...
		String absoluteFilePath = "";
		int count = 0;
		while (true) {
			File latestFile = getLatestFile(downloadDirectory);
			if (latestFile != null) {
				String fileName = latestFile.getName();
				if (!fileName.endsWith(".crdownload") && !fileName.endsWith(".tmp")) {
					UtilityObjectClass.getExtentTest().info("File " + fileName + " downloaded successfully.");
					absoluteFilePath = latestFile.getAbsolutePath();
					break;
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count++;
				if (count >= maxWaitTime) {
					UtilityObjectClass.getExtentTest().info("No files downloaded within the last " + maxWaitTime + " seconds.");
					break;
				}
			}
		}
		return absoluteFilePath;
	}

	public static File getLatestFile(String directoryPath) {
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();

		if (files == null || files.length == 0) {
			return null; // No files in the directory
		}

		// Find the latest file updated within the last 1 second
		File latestFile = null;
		long currentTime = System.currentTimeMillis();
		long lastModifiedThreshold = currentTime - 1000; // 1 second ago
		for (File file : files) {
			if (file.isFile() && file.lastModified() > lastModifiedThreshold) {
				latestFile = file;
				break; // Found the latest file updated within the last 1 second
			}
		}
		return latestFile;
	}

}
