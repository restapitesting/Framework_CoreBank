package org.tyss.flinkpay.testcases;

import java.util.List;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.tyss.flinkpay.genericutility.BaseClass;
import org.tyss.flinkpay.genericutility.IConstants;

@Listeners(org.tyss.flinkpay.genericutility.ListenerImplementationClass.class)
public class SimulateSwiftTxnsAndVerify_Inbound_Test extends BaseClass {
	@Test
	public void simulateSwiftTxnsAndVerify() {

		// step 1 : TRXN creation 
		List<String> filePaths = workflowUtility.SMART_SWIFT_TRXN_Creation_Simulation_tool("Txn_Info");

		// step 2 : TRXN Initiation 
		workflowUtility.initiateTrxns(filePaths);

		// step 3 : Read TRXNS From Master test data file
		List<String> txnList = excelUtility.getColumnDataBasedOnHeader(IConstants.SWIFT_EXCEL_DATA_FILEPATH, "Txn_Info", "TXN_REF_ID_VAR");
		
		// step 4 : Verify TRXN details in BANK GUI portal
		workflowUtility.verifySwiftTxnsInGUI(txnList);
		
		// step 5 : Verify TRXN details in Database layer
		workflowUtility.verifySwiftTxnsInDatabase(txnList);

		// step 6 : Verify TRXN details in API layer
		workflowUtility.verifySwiftTxnsInAPILayer(txnList);
		
	}
}
