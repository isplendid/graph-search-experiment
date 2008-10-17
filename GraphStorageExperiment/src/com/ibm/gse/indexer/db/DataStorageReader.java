/**
 * <copyright> 
 * 
 * Copyright (c) 2007-2008 IBM Corporation and others. 
 * All rights reserved. 
 * Project name: GraphStroageExperiment
 * </copyright> 
 * 
 * $ DataStorageReader.java, created: Sep 1, 2008 2:05:22 PM, author: niyuan $
 */

package com.ibm.gse.indexer.db;

import java.sql.Statement;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.gse.system.GraphStorage;
import com.ibm.iodt.sor.SPARQLQueryEngine;
import com.ibm.iodt.sor.query.SPARQLResultSet;
import com.ibm.iodt.sor.utils.Config;
import com.ibm.iodt.sor.utils.SORException;

public class DataStorageReader {
    final static String labelPredicate = "http://www.w3.org/2000/01/rdf-schema#comment";
    private SPARQLQueryEngine engine;
    private final String DEFAULT_KB = "default";
    private final String DATAGRAPH = "http://dbpedia.org";
    private final String TESTGRAPH = "rdfgraph";
    
    private Connection conn;
    
    public void init() {
        try {
            Config.setConfigFile(new FileInputStream("conf//sor-db2.cfg"));
            engine = new SPARQLQueryEngine(DEFAULT_KB);
            engine.setInference(false);
            
            Class.forName(GraphStorage.config.getStringSetting("driver", null)).newInstance();
            conn = DriverManager.getConnection(GraphStorage.config.getStringSetting("labeldatabaseURL", null), 
                                               GraphStorage.config.getStringSetting("user", null),
                                               GraphStorage.config.getStringSetting("password", null));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public SPARQLResultSet getSPARQLResultSet(String query) throws SORException {
        engine.setDefaultGraph(TESTGRAPH);
        return engine.getSPARQLResultSet(query);     
    }
    
    public ResultSet getSQLResultSet(String query) throws SQLException {
        Statement stat = conn.createStatement();
        return stat.executeQuery(query);
    }
    
    public boolean isToIndexed(String uri) throws SQLException {
        Statement stat = conn.createStatement();
        ResultSet rs =  stat.executeQuery("SELECT URI WHERE URI = \'" + uri + "\'");
        if(rs.next())
            return true;
        else
            return false;
    }
}
