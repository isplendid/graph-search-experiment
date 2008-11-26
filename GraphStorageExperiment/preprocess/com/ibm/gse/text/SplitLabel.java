/**
 * <copyright> 
 * 
 * Copyright (c) 2007-2008 IBM Corporation and others. 
 * All rights reserved. 
 * Project name: GraphStroageExperiment
 * </copyright> 
 * 
 * $ SplitLabel.java, created: Nov 10, 2008 6:00:37 PM, author: niyuan $
 */

package com.ibm.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashSet;

import com.ibm.iodt.sor.nt.NTLexer;
import com.ibm.iodt.sor.nt.NTParser;
import com.ibm.iodt.sor.nt.RDFLiteral;
import com.ibm.iodt.sor.nt.Term;
import com.ibm.iodt.sor.nt.URIReference;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class SplitLabel {
    public void split(String filename, String labelfile, String relfile) throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        BufferedWriter labelwr = new BufferedWriter(new FileWriter(labelfile));
        BufferedWriter relwr = new BufferedWriter(new FileWriter(relfile));
        
        Environment env;
        Database uri2kw;
        
        String dir = "D://eclipse-workplace//GraphStorageExperimentV2//LUBMdata//temp";
        EnvironmentConfig ec = new EnvironmentConfig();
        ec.setAllowCreate(true);
        ec.setTransactional(false);
        
        try {
            env = new Environment(new File(dir), ec);
        } catch (Exception e) {
            env = null;
            e.printStackTrace();
        }
        
        DatabaseConfig dc = new DatabaseConfig();
        dc.setAllowCreate(true);
        dc.setTransactional(false);
        
        try {
            uri2kw = env.openDatabase(null, dir, dc);
        } catch (DatabaseException e) {
            uri2kw = null;
            e.printStackTrace();
        }
        
               
        HashSet<String> types = new HashSet<String>();
        HashSet<String> nodes = new HashSet<String>();
        
        int counter = 0;
        NTLexer ntLexer = new NTLexer(input);
        NTParser ntParser = new NTParser(ntLexer);
        Term[] triple;
        triple = ntParser.line();
        int numberEdge = 0;
        int numberLabel = 0;
        int numberNode = 0;
        while(triple != null){
            String subject = ((URIReference) triple[0]).getUri();
            String predicate = ((URIReference) triple[1]).getUri();
        
            String object = (triple[2] instanceof URIReference) ? ((URIReference) triple[2])
                    .getUri() : ((RDFLiteral) triple[2]).getLexical();
            
            DatabaseEntry ude = new DatabaseEntry();
            DatabaseEntry kde = new DatabaseEntry();
                    
            StringBinding.stringToEntry(subject, ude);        
            OperationStatus os = uri2kw.get(null, ude, kde, LockMode.DEFAULT);
            
            if(os == OperationStatus.NOTFOUND){
                DatabaseEntry ude2 = new DatabaseEntry();
                DatabaseEntry wde2 = new DatabaseEntry();
        
                StringBinding.stringToEntry(subject, ude2);
                StringBinding.stringToEntry("0", wde2);
                uri2kw.put(null, ude2, wde2);
                numberNode++;
            }
            if(triple[2] instanceof URIReference) {
                os = uri2kw.get(null, ude, kde, LockMode.DEFAULT);
                if(os == OperationStatus.NOTFOUND){
                    DatabaseEntry ude3 = new DatabaseEntry();
                    DatabaseEntry wde3 = new DatabaseEntry();
            
                    StringBinding.stringToEntry(object, ude3);
                    StringBinding.stringToEntry("0", wde3);
                    uri2kw.put(null, ude3, wde3);
                    numberNode++;
                }
            }
            if(predicate.equals("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name")) {
                labelwr.append("<" + subject + ">");
                labelwr.append("\t" + object + "\n");
                numberLabel++;
            } else if(triple[2] instanceof URIReference) {
                relwr.append("<" + subject + "> ");
                relwr.append("<" + predicate + "> ");
                relwr.append("<" + object + "> \n");
                numberEdge++;
            } else {
                relwr.append("<" + subject + "> ");
                relwr.append("<" + predicate + "> ");
                relwr.append("<http://blacknode" + counter + "> \n");
                numberEdge++;
                
                labelwr.append("<http://blacknode" + counter + ">");
                labelwr.append("\t" + object + "\n");
                numberLabel++;
                counter++;
            }     
            if(predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") && !types.contains(object)) {
                int pos = object.lastIndexOf('#');
                String label = object.substring(pos + 1);
                labelwr.append("<" + object + ">");
                labelwr.append("\t" + label + "\n");
                numberLabel++;
                types.add(object);
            }
            triple = ntParser.line();
        }
          input.close();
          System.out.println("The number of nodes is " + numberNode);
          System.out.println("The number of labels is " + numberLabel);
          System.out.println("The number of edges is " + numberEdge);
          
        relwr.flush();
        relwr.close();
        labelwr.flush();
        labelwr.close();
    }
    
    public static void main(String[] args) throws Exception {
        SplitLabel sl = new SplitLabel();        
        sl.split(args[0], args[1], args[2]);
    }
}
