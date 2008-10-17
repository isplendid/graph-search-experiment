package com.ibm.gse.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.ibm.gse.system.GraphStorage;

public class DatabaseLoader {
	
	public void load(String filename) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(filename));
		String temp;
		String strDrv = GraphStorage.config.getStringSetting("Driver", null);
		String strUser = GraphStorage.config.getStringSetting("User", null);
		String strDatabase = GraphStorage.config.getStringSetting("Database", null);
		String strPw = GraphStorage.config.getStringSetting("Password", null);
		
		System.out.println("Begin connection ...");
		
		Class.forName(strDrv);
		
		
		Connection con = DriverManager.getConnection(strDatabase, strUser, strPw);
		
		Statement stm = con.createStatement();
		
		System.out.println("Creating table ...");
		
//		stm.executeUpdate("DROP TABLE texttable");
		stm.executeUpdate("CREATE TABLE texttable (uri VARCHAR(254) NOT NULL, text VARCHAR(2000), PRIMARY KEY(uri))");
//		stm.executeUpdate("DELETE FROM texttable");
		
		System.out.println("Begin loading ...");
		int counter = 0;
		
		while ((temp = rd.readLine()) != null) {
			String[] split = temp.split("\t");
			
			try {
				stm.executeUpdate("INSERT INTO texttable VALUES('" + split[0] + "', '" + split[1] + "')");
				counter ++;
				if (counter % 1000 == 0) System.out.println(counter);
			} catch (Exception e) {
				
//				System.out.println(temp);
//				System.out.println("INSERT INTO texttable VALUES('" + split[0] + "', '" + split[1] + "')");
//				e.printStackTrace();
				
				continue;
			}
		}
		
		stm.close();
		
		con.close();
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DatabaseLoader ld = new DatabaseLoader();
		
		ld.load(args[0]);
	}

}
