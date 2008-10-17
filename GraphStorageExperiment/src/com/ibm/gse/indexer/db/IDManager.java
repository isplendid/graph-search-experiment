package com.ibm.gse.indexer.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibm.gse.system.ConnectionFactory;

public class IDManager {

	final static String COLUMN_PATTERN = "URI";
	final static String TABLE_URI2ID = "URI2ID";
	
	static int idCounter = 0;
	
	private Connection conn = null;
    
    public IDManager() {
    	conn = ConnectionFactory.createConnection();
    }
    
    /** initialize the index storage, to create the necessary tables*/
    public void install() {  
        
	}
    
    /** return the uri of the resource given the id */
    public String getURI(int id) {
        String uri = null;
        try {
            Statement statement = conn.createStatement();
            String selectSQL = "SELECT " + COLUMN_PATTERN + " FROM " + TABLE_URI2ID + " WHERE ID = " + id;
            ResultSet rs = statement.executeQuery(selectSQL);
            if(rs.next())
                uri = rs.getString(1);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uri;
    }
    
    /** return the id of the resource given the uri */
    public int getID(String uri) {
        int id = -1;
        try {
            Statement statement = conn.createStatement();
            String selectSQL = "SELECT ID FROM " + TABLE_URI2ID + " WHERE URI = \'" + uri + "\'";
            ResultSet rs = statement.executeQuery(selectSQL);
            if(rs.next())
                id = rs.getInt(1);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    
    public int addURI(String uri) {
        try {
            Statement statement = conn.createStatement();
            String insertSQL = "INSERT INTO " + TABLE_URI2ID + " VALUES (\'" + uri + "\'," + idCounter + ")";
            statement.executeUpdate(insertSQL);
            idCounter++;
            statement.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idCounter - 1;
    }

}
