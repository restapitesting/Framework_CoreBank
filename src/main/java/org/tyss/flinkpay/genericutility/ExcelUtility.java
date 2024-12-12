package org.tyss.flinkpay.genericutility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtility {

	public String getDataFromExcelCellBasedOnUniqueDataAndHeader(String excelPath, String sheetName, String uniqueData, String header){
		String value = "";
		try {
			FileInputStream fis = new FileInputStream(excelPath);
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet sheet = workbook.getSheet(sheetName);
			int rowCount = sheet.getPhysicalNumberOfRows();
			for (int i = 1; i < rowCount; i++) {
				String actualUniqueData = sheet.getRow(i).getCell(0).getStringCellValue();
				if (actualUniqueData.equalsIgnoreCase(uniqueData)) {
					int columnCount = sheet.getRow(i).getPhysicalNumberOfCells();
					for (int j = 1; j < columnCount; j++) {
						String actualHeader = sheet.getRow(0).getCell(j).getStringCellValue();
						if (actualHeader.equalsIgnoreCase(header)) {
							value = sheet.getRow(i).getCell(j).getStringCellValue();
							break;
						}
					}
					if (!value.isEmpty()) {
						break;
					}
				}
			}
			workbook.close();
			fis.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public List<String> generateAndWriteDataToExcelForJson(String filePath, String sheetName){
		JavaUtility javaUtility = new JavaUtility();
		InputStream inputStream;
		List<String> transactionsList = new LinkedList<String>();
		try {
			inputStream = new FileInputStream(filePath);
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheet(sheetName);
			DataFormatter df = new DataFormatter();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			int rowCount = sheet.getPhysicalNumberOfRows();
			int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();

			int tatDateColumn = 0 ;
			for (int i = 0; i < columnCount; i++) {
				if (df.formatCellValue(sheet.getRow(0).getCell(i), evaluator).trim().equalsIgnoreCase("TAT_DATE")) {
					tatDateColumn = i;
					break;
				}
			}

			for (int i = 1; i < rowCount; i++) {
				String tatDate = df.formatCellValue(sheet.getRow(i).getCell(tatDateColumn), evaluator).trim();
				String transactionId = "TR"+javaUtility.generateDateWithOffset(tatDate,"yyyyMMddhhmmssSSS");
				transactionsList.add(transactionId);
				String transctionDate = javaUtility.generateDateWithOffset(tatDate,"dd-MM-yyyy") ;
				String transactionTime = javaUtility.generateDateWithOffset(tatDate,"hh:mm:ss:SSS");

				for (int j = 0; j < columnCount; j++) {
					String cellData = df.formatCellValue(sheet.getRow(0).getCell(j), evaluator);

					if (cellData.equalsIgnoreCase("TXN_ID_VAR")) {
						sheet.getRow(i).getCell(j).setCellValue(transactionId);
					} else if(cellData.equalsIgnoreCase("TXN_DATE_VAR")) {
						sheet.getRow(i).getCell(j).setCellValue(transctionDate);
					} else if(cellData.equalsIgnoreCase("TXN_TIME_VAR")) {
						sheet.getRow(i).getCell(j).setCellValue(transactionTime);
					}
				}
				Thread.sleep(100);
			}
			OutputStream outputStream = new FileOutputStream(filePath);
			workbook.write(outputStream);
			workbook.close();
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionsList;
	}


	public List<Map<String, String>> getDataFromExcel(String excelPath, String sheetName) {
		List<Map<String, String>> entireData = new LinkedList<>();

		try (InputStream inputStream = new FileInputStream(excelPath);
				Workbook workbook = WorkbookFactory.create(inputStream)) {

			Sheet sheet = workbook.getSheet(sheetName);

			// Get the header row (first row)
			Row headerRow = sheet.getRow(0);
			if (headerRow == null) {
				throw new IllegalArgumentException("The sheet is empty.");
			}

			int rowCount = sheet.getLastRowNum(); // Total number of rows
			int columnCount = headerRow.getLastCellNum(); // Total number of columns

			// Iterate over the rows starting from the second row (data rows)
			for (int i = 1; i <= rowCount; i++) {
				Row currentRow = sheet.getRow(i);
				if (currentRow == null) {
					continue; // Skip empty rows
				}

				Map<String, String> singleRowData = new LinkedHashMap<>();
				for (int j = 0; j < columnCount; j++) {
					// Get the header for the current column
					String header = getCellValue(headerRow.getCell(j));

					// Get the cell value, handling long numbers properly
					Cell cell = currentRow.getCell(j);
					String cellValue = cell == null ? "" : getCellValue(cell);

					singleRowData.put(header, cellValue);
				}
				entireData.add(singleRowData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return entireData;
	}

	// Method to get cell value while avoiding scientific notation for long numbers
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				// Use BigDecimal to prevent scientific notation for large numbers
				return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
			}
		case BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return "";
		default:
			return "";
		}
	}

	public void generateAndWriteDataToExcelForSwift(String filePath, String sheetName) {
		JavaUtility javaUtility = new JavaUtility();
		try {
			InputStream inputStream = new FileInputStream(filePath);
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheet(sheetName);
			DataFormatter df = new DataFormatter();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			int rowCount = sheet.getPhysicalNumberOfRows();
			int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();

			int tatDateColumn = 0 ;
			for (int i = 0; i < columnCount; i++) {
				if (df.formatCellValue(sheet.getRow(0).getCell(i), evaluator).trim().equalsIgnoreCase("TAT_DATE")) {
					tatDateColumn = i;
					break;
				}
			}

			for (int i = 1; i < rowCount; i++) {
				String tatDate = df.formatCellValue(sheet.getRow(i).getCell(tatDateColumn), evaluator).trim();
				String txnRefId = "TR"+javaUtility.generateDateWithOffset(tatDate,"yyyyMMddhhmmssSSS");
				String txnDateTime = javaUtility.generateDateWithOffset(tatDate,"yyyy-MM-dd'T'HH:mm:ss") ;
				String paymentSettlementDate = javaUtility.generateDateWithOffset(tatDate,"yyyy-MM-dd");

				for (int j = 0; j < columnCount; j++) {
					String cellData = df.formatCellValue(sheet.getRow(0).getCell(j), evaluator);

					if (cellData.equalsIgnoreCase("TXN_REF_ID_VAR")) {
						sheet.getRow(i).getCell(j).setCellValue(txnRefId);
					} else if(cellData.equalsIgnoreCase("TXN_DATE_TIME_VAR")) {
						sheet.getRow(i).getCell(j).setCellValue(txnDateTime);
					} else if(cellData.equalsIgnoreCase("PAYMENT_SETTLEMENT_DATE_VAR")) {
						sheet.getRow(i).getCell(j).setCellValue(paymentSettlementDate);
					}
				}
				Thread.sleep(100);
			}
			OutputStream outputStream = new FileOutputStream(filePath);
			workbook.write(outputStream);
			workbook.close();
			inputStream.close();
			outputStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getEntireTcDataBasedOnTcId(String excelPath, String sheetName,
			String tcId){
		Map<String, String> map = new LinkedHashMap<String, String>();
		try(FileInputStream fis = new FileInputStream(excelPath)) {
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet sheet = workbook.getSheet(sheetName);
			String value = "";
			String actualTestCaseName = "";
			String actualKey = "";

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				try {
					actualTestCaseName = sheet.getRow(i).getCell(0).getStringCellValue();
				} catch (Exception e) {
					UtilityObjectClass.getExtentTest().warning("Error reading test case name at row: " + i);
				}

				if (actualTestCaseName.equalsIgnoreCase(tcId)) {
					for (int j = 0; j <= sheet.getRow(0).getLastCellNum(); j++) {
						try {
							actualKey = sheet.getRow(0).getCell(j).toString();
							try {
								value = sheet.getRow(i).getCell(j).toString();
							} catch (Exception e) {
							}
							map.put(actualKey.trim(), value.trim());
						} catch (Exception e) {
						}
					}
					break;
				}
			}
			workbook.close();
		}catch (Exception e) {
			// TODO: handle exception
		}

		if (map.isEmpty()) {
			UtilityObjectClass.getExtentTest().warning("No data found for Test Case ID: " + tcId);
		} else {
			UtilityObjectClass.getExtentTest().info("Data retrieved for Test Case ID: " + tcId + " - " + map.toString());
		}
		return map;
	}

	public List<String> getTxnDataFromExcelBasedOnIndex(String excelPath, String sheetName, int index) {
		List< String> entireData = new LinkedList<>();

		try (InputStream inputStream = new FileInputStream(excelPath);
				Workbook workbook = WorkbookFactory.create(inputStream)) {

			Sheet sheet = workbook.getSheet(sheetName);
			Row row = sheet.getRow(index+1);

			int columCount = row.getPhysicalNumberOfCells();
			for (int i = 1; i < columCount; i++) {
				entireData.add(row.getCell(i).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entireData;
	}
	
	
	public List<String> getColumnDataBasedOnHeader(String excelPath, String sheetName, String header) {
		List< String> columnData = new LinkedList<>();

		try (InputStream inputStream = new FileInputStream(excelPath);
				Workbook workbook = WorkbookFactory.create(inputStream)) {

			Sheet sheet = workbook.getSheet(sheetName);
			int rowCont = sheet.getLastRowNum();
			int cellCount = sheet.getRow(0).getLastCellNum();
			int cellIndex = 0;
			
			for (int i = 0; i < cellCount; i++) {
				if (sheet.getRow(0).getCell(i).toString().equalsIgnoreCase(header)) {
					cellIndex = i;
					break;
				}
			}
			
			for (int i = 1; i <= rowCont; i++) {
				columnData.add(sheet.getRow(i).getCell(cellIndex).toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnData;
	}
}
