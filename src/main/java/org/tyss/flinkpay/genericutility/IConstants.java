package org.tyss.flinkpay.genericutility;

public interface IConstants {

	// Common Data file path
	String COMMON_DATA_FILEPATH = ".\\CommonData.properties";

	// Outbound data file path
	String OUTBOUND_DATA_EXCEL_FILEPATH = ".\\Outbound_Data.xlsx";
	
	// Swift simulation file paths- Flink Pay
	String SWIFT_SCHEMA_FILEPATH = ".\\Swift_Simuation_Files\\Swift_Message_SchemaFile.txt";
	String SWIFT_EXCEL_DATA_FILEPATH = ".\\Swift_Simuation_Files\\Swift_Message_DataFile.xlsx";
	String GENERATED_SWIFT_MESSAGEPATH = ".\\Swift_Simuation_Files\\";

	// Json simulation file paths - Ninza Pay
	String JSON_SCHEMA_FILEPATH = ".\\Json_Simualtion_Files\\Json_SchemaFile.txt";
	String JSON_EXCEL_DATA_FILEPATH = ".\\Json_Simualtion_Files\\Json_DataFile.xlsx";
	String GENERATED_JSON_FILEPATH = ".\\Json_Simualtion_Files";

	// Wait times
	int IMPLICIT_WAIT_TIME = 10;
	int EXPLICIT_WAIT_TIME = 10;

}
