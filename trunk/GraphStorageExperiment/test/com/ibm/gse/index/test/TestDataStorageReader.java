package com.ibm.gse.index.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibm.gse.indexer.StorageReader;
import com.ibm.gse.system.ConnectionFactory;

public class TestDataStorageReader implements StorageReader {
	Connection conn;
	Statement stm;
	
	public TestDataStorageReader() {
		conn = ConnectionFactory.createConnection();
	}

	public ResultSet getSQLResultSet(String string) {
		try {
			stm = conn.createStatement();
			return stm.executeQuery(string);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void closeStat() {
		try {
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
//			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
