package com.ibm.gse.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	public static Connection createConnection() {
		try {
			String driver = GraphStorage.config.getStringSetting("Driver", "com.ibm.db2.jcc.DB2Driver");
			String database = GraphStorage.config.getStringSetting("Database", null);
			String user = GraphStorage.config.getStringSetting("User", null);
			String pw = GraphStorage.config.getStringSetting("Password", null);
			
			if (database == null || user == null || pw == null) return null;
			
			Class.forName(driver);
			
			return DriverManager.getConnection(database, user, pw);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
