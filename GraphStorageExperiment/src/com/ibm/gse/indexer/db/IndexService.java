/**
 * <copyright> 
 * 
 * Copyright (c) 2007-2008 IBM Corporation and others. 
 * All rights reserved. 
 * Project name: GraphStroageExperiment
 * </copyright> 
 * 
 * $ IndexService.java, created: Aug 29, 2008 5:12:04 PM, author: niyuan $
 */

package com.ibm.gse.indexer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PriorityQueue;

import com.ibm.gse.pattern.HashingPatternCodec;
import com.ibm.gse.pattern.ModHash;
import com.ibm.gse.pattern.PatternCodec;
import com.ibm.gse.struct.ConcreteQueryGraphNode;
import com.ibm.gse.struct.Pattern;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.system.GraphStorage;
import com.ibm.iodt.sor.query.SPARQLResultSet;
import com.ibm.iodt.sor.utils.SORException;

public class IndexService {
    
    IndexStorage indexStorage;
    DataStorageReader dataReader;
    PatternCodec codec;
    
    public IndexService(IndexStorage indexstorage, DataStorageReader dsr) {
        try {
            this.indexStorage = indexstorage;
            this.dataReader = dsr;
            codec = new HashingPatternCodec(new ModHash());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void indexNode() {
        try {
            ResultSet rs = dataReader.getSQLResultSet("SELECT * FROM TEXTTABLE");
            int counter = 0;
            while(rs.next()) {
                if(counter % 1000 == 0)
                    System.out.println("node " + counter);
                counter++;
                String uri = rs.getString(1);
                String label = rs.getString(2);
                String[] terms = label.split(" ");
                int id = indexStorage.getID(uri);
                if(id == -1)
                    id = indexStorage.addURI(uri);
                for(int i = 0; i < terms.length; i++) {
                    QueryGraph g = new QueryGraph();
                    g.addNode(terms[i]);
                    indexStorage.addNode(codec.encodePattern(g), id);
                }
            }
            rs.close();
            
            ResultSet keys = indexStorage.getPatterns(1);
            while(keys.next()) {
                String key = keys.getString(1);
                int count = indexStorage.getPatternCount(key, 1);
                indexStorage.addStat(key, count);
            }
            keys.close();
            indexStorage.closeStat();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void indexEdge() {
        try {
            ResultSet rs1 = dataReader.getSQLResultSet("SELECT URI, TEXT FROM TEXTTABLE");
            
            int counter = 0;
            while(rs1.next()) {
                if(counter % 1000 == 0)
                    System.out.println("Edge " + counter);
                counter++;
                String uri = rs1.getString(1);
                String label = rs1.getString(2);
                String[] terms1 = label.split(" ");
                SPARQLResultSet rs2 = dataReader.getSPARQLResultSet("select ?p ?o where {" + uri + " ?p ?o}");
                while(rs2.next()) {
                    String pre = rs2.getString(0);
                    String obj = rs2.getString(1);
                    ResultSet rs3 = dataReader.getSQLResultSet("SELECT URI, TEXT FROM TEXTTABLE WHERE URI = \'" + obj + "\'");
                    if(rs3.next()) {
                        String label2 = rs3.getString(2);
                        String[] terms2 = label2.split(" ");                        
                        
                        for(int i = 0; i < terms1.length; i++) {
                            for(int j = 0; j < terms2.length; j++) {
                                QueryGraph pattern = new QueryGraph();
                                ConcreteQueryGraphNode n1 = new ConcreteQueryGraphNode(terms1[i]);
                                ConcreteQueryGraphNode n2 = new ConcreteQueryGraphNode(terms2[j]);
                                pattern.addNode(n1);
                                pattern.addNode(n2);
                                pattern.addEdge(n1, n2, pre);
                                String key = codec.encodePattern(pattern);
                                if(terms1[i].compareTo(terms2[j]) <= 0) {
                                    indexStorage.addEdge(key, indexStorage.getID(uri), indexStorage.getID(obj));
                                } else {
                                    indexStorage.addEdge(key, indexStorage.getID(obj), indexStorage.getID(uri));
                                }
                            }
                        }                        
                    }
                    rs3.close();
                }
                rs2.close();
            }
            rs1.close();
            ResultSet keys = indexStorage.getPatterns(2);
            while(keys.next()) {
                String key = keys.getString(1);
                int count = indexStorage.getPatternCount(key, 2);
                indexStorage.addStat(key, count);
            }
            keys.close();
            indexStorage.closeStat();
        } catch (SORException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }        
    }
    
    public void indexPatterns(int threshold) {
        try{
            PriorityQueue<Pattern> pq = new PriorityQueue<Pattern>();
            ResultSet rs = indexStorage.getAllStat();
            if(rs != null) {
                while(rs.next()) {
                    pq.add(new Pattern(rs.getString(1), rs.getInt(2)));
                }
            }
            while(threshold > 0 && pq.size() > 0) {
                Pattern p1 = pq.poll();
                Pattern p2 = pq.poll();
                QueryGraph q1 = codec.decodePattern(p1.getPatternCode());
                QueryGraph q2 = codec.decodePattern(p2.getPatternCode());
                
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void buildIndex(int threshold) {
        
    }
    
    public static void main(String[] args) throws Exception  {
        
//        String kw = ":<Database> insurance industry";
//        kw = kw.replaceAll("<", "&lt");
//        kw = kw.replaceAll(">", "&gt");
//        System.out.println(kw);
        
        GraphStorage gs = new GraphStorage();
        IndexStorage is = new IndexStorage();
        PatternCodec codec = new HashingPatternCodec(new ModHash());
        
        /* test get all statistic */
//        ResultSet rs = is.getAllStat();
//        while(rs.next()) {
//            System.out.println(rs.getString(1));
//            System.out.println(rs.getInt(2));
//        }
//        rs.close();
        
        /* test get patterns */
//        QueryGraph q = new QueryGraph();
//        QueryGraphNode n1 = q.addNode("insurance");
//        QueryGraphNode n2 = q.addNode("insurance");
//        q.addEdge(n1, n2, "http://predicate1");
//        List<List<Integer>> rs2 = is.getPatterns(codec.encodePattern(q), 2);
//        for(int i = 0; i < rs2.size(); i++) {
//            List<Integer> p = rs2.get(i);
//            for(int j = 0; j < p.size(); j++) {
//                System.out.print(p.get(j) + " ");
//            }
//            System.out.println();
//        }
        
        is.uninstall();
        is.install();
        DataStorageReader dsr = new DataStorageReader();
        dsr.init();
        IndexService indexer = new IndexService(is, dsr);
        System.out.println("To index the node...");
        indexer.indexNode();
        System.out.println("To index the edge...");
        indexer.indexEdge();
        is.close();
    }
}
