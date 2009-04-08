/**
 * <copyright> 
 * 
 * Copyright (c) 2007-2008 IBM Corporation and others. 
 * All rights reserved. 
 * Project name: GraphStorageExperiment
 * </copyright> 
 * 
 * $ IndexStorage.java, created: Aug 29, 2008 5:06:56 PM, author: niyuan $
 */

package sjtu.apex.gse.indexer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sjtu.apex.gse.system.ConnectionFactory;
import sjtu.apex.gse.system.GraphStorage;


public class IndexStorage {

    final static String COLUMN_PATTERN = "PATTERN";
    final static String COLUMN_NODE_PREFIX = "NODE";
    
    final static String TABLE_URI2ID = "URI2ID";
    final static String TABLE_STATISTIC = "STATISTIC";
    final static String TABLE_PATTERNS_PREFIX = "PATTERN";
    
    static int idCounter = 0;
    
    private Connection conn = null;
    private Statement stat = null;
    
    static int insertCounter = 0;
    
    /** set the connection of db**/
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = ConnectionFactory.createConnection(); 
            conn.setAutoCommit(false);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return conn;
    }
    
    /** initialize the index storage, to create the necessary tables*/
    public void install() {  
        int sqlCode=0;      // Variable to hold SQLCODE        
        String sqlState="00000";  // Variable to hold SQLSTATE
        try { 
            if(conn == null)
                conn = getConnection();
            Statement statement = conn.createStatement();
            String createSQL = "CREATE TABLE " + TABLE_URI2ID + " (URI VARCHAR(255) NOT NULL, ID INTEGER NOT NULL, PRIMARY KEY(URI, ID))";
            statement.executeUpdate(createSQL);
            
            createSQL = "CREATE TABLE " + TABLE_STATISTIC + " (" + COLUMN_PATTERN + " VARCHAR(1000) NOT NULL, COUNT INTEGER NOT NULL, PRIMARY KEY(PATTERN))";
            statement.executeUpdate(createSQL);
            
            statement.close();
            for(int i = 1; i < 6; i++)
                createTable(i);
            conn.commit();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
        }
    }
    
    /** drop all the tables */
    public void uninstall() {
        int sqlCode=0;      // Variable to hold SQLCODE        
        String sqlState="00000";  // Variable to hold SQLSTATE
        try {   
            if(conn == null)
                conn = getConnection();
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();
            String dropSQL = "DROP TABLE " + TABLE_URI2ID;
            statement.executeUpdate(dropSQL);
            
            dropSQL = "DROP TABLE " + TABLE_STATISTIC;
            statement.executeUpdate(dropSQL);
            
            for(int i = 1; i < 6; i++) {
                dropSQL = "DROP TABLE " + TABLE_PATTERNS_PREFIX + i;
                statement.execute(dropSQL);
            }
            statement.close();
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE    
            if(sqlCode != -204) {
            	System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            	e.printStackTrace();
            }
        }
    }
    
    /** to create the pattern table for numberofNode nodes */
    public void createTable(int numberofNode) {
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String createSQL = "CREATE TABLE " + TABLE_PATTERNS_PREFIX + numberofNode
                               + " (" + COLUMN_PATTERN + " VARCHAR(1000) NOT NULL, ";
            for(int i = 0; i < numberofNode; i++) {
                createSQL += COLUMN_NODE_PREFIX + i + " INTEGER NOT NULL, ";
            }
            createSQL += "PRIMARY KEY(PATTERN, ";
            for(int i = 0; i < numberofNode - 1; i++) {
                createSQL += COLUMN_NODE_PREFIX + i + ",";
            }
            createSQL += COLUMN_NODE_PREFIX + (numberofNode - 1) + "))";
            statement.executeUpdate(createSQL);
            statement.close();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
        }
    }
    
    /** return the uri of the resource given the id */
    public String getURI(int id) {
        String uri = null;
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String selectSQL = "SELECT " + COLUMN_PATTERN + " FROM " + TABLE_URI2ID + " WHERE ID = " + id;
            ResultSet rs = statement.executeQuery(selectSQL);
            if(rs.next())
                uri = rs.getString(1);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return uri;
    }
    
    /** return the id of the resource given the uri */
    public int getID(String uri) {
        int id = -1;
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String selectSQL = "SELECT ID FROM " + TABLE_URI2ID + " WHERE URI = \'" + uri + "\'";
            ResultSet rs = statement.executeQuery(selectSQL);
            if(rs.next())
                id = rs.getInt(1);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return id;
    }
    
    /** return the statistic given a pattern */
    public int getStat(String key) {
        int count = -1;
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String selectSQL = "SELECT COUNT FROM " + TABLE_STATISTIC+ " WHERE " + COLUMN_PATTERN +  "= \'" + key + "\'";
            ResultSet rs = statement.executeQuery(selectSQL);
            if(rs.next())
                count = rs.getInt(1);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return count;
    }
    
    /** return all statistic of current existing patterns */
    public ResultSet getAllStat() {
        ResultSet rs = null;
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            stat = conn.createStatement();
            String selectSQL = "SELECT " + COLUMN_PATTERN + ", COUNT FROM " + TABLE_STATISTIC;
            rs = stat.executeQuery(selectSQL);

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }   
        return rs;
    }
    
    /** return all keys in a pattern table */
    public ResultSet getPatterns(int numberofNode) {
        ResultSet rs = null;
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            stat = conn.createStatement();
            String selectSQL = "SELECT DISTINCT " + COLUMN_PATTERN + " FROM " + TABLE_PATTERNS_PREFIX + numberofNode;
            rs = stat.executeQuery(selectSQL);
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return rs;
    }
    
    /** return the cardinality of a pattern */
    public int getPatternCount(String key, int numberofNode) {
        int count = 0;
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String countSQL = "SELECT COUNT(" + COLUMN_PATTERN + ") FROM " + TABLE_PATTERNS_PREFIX + numberofNode
                               + " WHERE " + COLUMN_PATTERN + " = \'" + key + "\'";
            ResultSet rs = statement.executeQuery(countSQL);
            if(rs.next())
                count = rs.getInt(1);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return count;
    }
    
    /** return the patterns give the sequence of the string */
    public List<List<Integer>> getPatterns(String key, int numberofNode) {
        List<List<Integer>> patterns = new ArrayList<List<Integer>>();
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String selectSQL = "SELECT ";
            for(int i = 0; i < numberofNode - 1; i++) {
                selectSQL += COLUMN_NODE_PREFIX + i + ", ";
            }
            selectSQL += COLUMN_NODE_PREFIX + (numberofNode - 1) + " FROM " + TABLE_PATTERNS_PREFIX + numberofNode + 
                         " WHERE " + COLUMN_PATTERN + " = \'" + key + "\'";                	
            ResultSet rs = statement.executeQuery(selectSQL);
            while(rs.next()) {
                List<Integer> record = new ArrayList<Integer>();
                for(int i = 0; i < numberofNode; i++) {
                    record.add(rs.getInt(i+1));
                }
                patterns.add(record);
            }        
            rs.close();
            statement.close();
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return patterns;
    }
    
    /** add a new URI to the URI2ID mapping */
    public int addURI(String uri) {
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String insertSQL = "INSERT INTO " + TABLE_URI2ID + " VALUES (\'" + uri + "\'," + idCounter + ")";
            statement.executeUpdate(insertSQL);
            idCounter++;
            statement.close();
            
            insertCounter++;
            if(insertCounter == 10000) {
                conn.commit();
                insertCounter = 0;
            }
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return idCounter - 1;
    }
    
    /** add a statistic to the statistic */
    public void addStat(String key, int count) {
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String insertSQL = "INSERT INTO " + TABLE_STATISTIC + " VALUES (\'" + key + "\'," + count + ")";
            statement.executeUpdate(insertSQL);
            statement.close();
            insertCounter++;
            if(insertCounter == 10000) {
                conn.commit();
                insertCounter = 0;
            }
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE                
            sqlState = e.getSQLState(); // Get SQLSTATE                                            
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /** add an indexed node to the storage */
    public void addNode(String key, int nodeid) {
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String insertSQL = "INSERT INTO " + TABLE_PATTERNS_PREFIX + "1" + " VALUES (\'" + key + "\'," + nodeid + ")";
            statement.executeUpdate(insertSQL);
            statement.close();
            insertCounter++;
            if(insertCounter == 10000) {
                conn.commit();
                insertCounter = 0;
            }
        } catch (SQLException e) {
            if(e.getErrorCode() != -803) {
                sqlCode = e.getErrorCode(); // Get SQLCODE                
                sqlState = e.getSQLState(); // Get SQLSTATE                                            
                System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }        
    }
    
    /** add an indexed edge to the storage */
    public void addEdge(String key, int nodeid1, int nodeid2) {
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String insertSQL = "INSERT INTO " + TABLE_PATTERNS_PREFIX + "2" + " VALUES (\'" + key + "\'," 
                               + nodeid1 + "," + nodeid2 + ")";
            statement.executeUpdate(insertSQL);
            statement.close();
            insertCounter++;
            if(insertCounter == 10000) {
                conn.commit();
                insertCounter = 0;
            }
        } catch (SQLException e) {
            if(e.getErrorCode() != -803) {
                sqlCode = e.getErrorCode(); // Get SQLCODE                
                sqlState = e.getSQLState(); // Get SQLSTATE                                            
                System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }        
    }
    
    /** add an indexed pattern to the storage */
    public void addPattern(String key, int numberofNode, ArrayList<Integer> pattern) {
        int sqlCode = 0;
        String sqlState = "00000";
        try {
            if(conn == null) {
                conn = getConnection();
            }
            Statement statement = conn.createStatement();
            String insertSQL = "INSERT INTO " + TABLE_PATTERNS_PREFIX + numberofNode + " VALUES (\'" + key + "\',";
            for(int i = 0; i < numberofNode - 1; i++) {
                insertSQL += pattern.get(i) + ",";
            }
            insertSQL += pattern.get(numberofNode - 1) + ")";
            statement.executeUpdate(insertSQL);
            statement.close();
            insertCounter++;
            if(insertCounter == 10000) {
                conn.commit();
                insertCounter = 0;
            }
        } catch (SQLException e) {
            if(e.getErrorCode() != -803) {
                sqlCode = e.getErrorCode(); // Get SQLCODE                
                sqlState = e.getSQLState(); // Get SQLSTATE                                            
                System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }        
    }
    
    public void closeStat() {
        try {
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
