package org.tyss.flinkpay.genericutility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.jdbc.Driver;


public class DatabaseUtility {

	/**
	 * This method will perform the mysql database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection connectDB(String dbUrl, String dbUsername, String dbPassword) {
		Connection connection = null;
		try {
			Driver driver = new Driver();
			DriverManager.registerDriver(driver);
			connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;

	}

	/**
	 * This method will close the mysql database
	 * 
	 * @throws SQLException
	 */
	public void closeDB(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will execute the query
	 * 
	 * @param query
	 * @return
	 * @throws Throwable
	 */
	public ResultSet executeQuery(String query, Connection connection) throws Throwable {
		ResultSet result = connection.createStatement().executeQuery(query);
		return result;
	}

	/**
	 * This method will execute the query
	 * 
	 * @param query
	 * @return
	 * @throws Throwable
	 */
	public int executeUpdate(String query, Connection connection) throws Throwable {
		int result = connection.createStatement().executeUpdate(query);
		return result;
	}

	/**
	 * This method will execute query based on query and it will verify the data.
	 * 
	 * @param querry
	 * @param columnName
	 * @param expectedData
	 * @return
	 * @throws Throwable
	 */
	public boolean verifyColumnContainsData(Connection connection, String tableName, String columnName,
			String expectedData) {
		boolean flag = false;
		try {
			ResultSet resultSet = connection.createStatement()
					.executeQuery("select " + columnName + " from " + tableName + ";");
			
			UtilityObjectClass.getExtentTest().info(" Query ==>"+"select " + columnName + " from " + tableName + ";");
			while (resultSet.next()) {
				if (resultSet.getString(columnName).equals(expectedData)) {
					flag = true;
					break;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}
}
