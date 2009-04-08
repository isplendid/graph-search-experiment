package sjtu.apex.gse.db.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import sjtu.apex.gse.system.ConnectionFactory;


public class SQLExecutor {
	
	static String sql = "DELETE FROM URI2ID";
//	static String sql = "CREATE TABLE URI2ID (URI VARCHAR(255) NOT NULL, ID INTEGER NOT NULL, PRIMARY KEY(ID))";

	public static void main(String[] args) {
		Connection conn;
		Statement stm;
		
		try {
			conn = ConnectionFactory.createConnection();
			stm = conn.createStatement();
			stm.execute(sql);
			stm.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
