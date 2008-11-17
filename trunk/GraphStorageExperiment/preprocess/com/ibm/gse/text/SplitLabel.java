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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.ibm.iodt.sor.nt.NTLexer;
import com.ibm.iodt.sor.nt.NTParser;
import com.ibm.iodt.sor.nt.RDFLiteral;
import com.ibm.iodt.sor.nt.Term;
import com.ibm.iodt.sor.nt.URIReference;

public class SplitLabel {
    public void split(String filename, String labelfile, String relfile) throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        BufferedWriter labelwr = new BufferedWriter(new FileWriter(labelfile));
        BufferedWriter relwr = new BufferedWriter(new FileWriter(relfile));
        
        int counter = 0;
        NTLexer ntLexer = new NTLexer(input);
        NTParser ntParser = new NTParser(ntLexer);
        Term[] triple;
        triple = ntParser.line();
        while(triple != null){
            String subject = ((URIReference) triple[0]).getUri();
            String predicate = ((URIReference) triple[1]).getUri();
        
            String object = (triple[2] instanceof URIReference) ? ((URIReference) triple[2])
                    .getUri() : ((RDFLiteral) triple[2]).getLexical();
            if(predicate.equals("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name")) {
                labelwr.append("<" + subject + ">");
                labelwr.append("\t" + object + "\n");
            } else if(triple[2] instanceof URIReference) {
                relwr.append("<" + subject + "> ");
                relwr.append("<" + predicate + "> ");
                relwr.append("<" + object + "> \n");
            } else {
                relwr.append("<" + subject + "> ");
                relwr.append("<" + predicate + "> ");
                relwr.append("<http://blacknode" + counter + "> \n");
                
                labelwr.append("<http://blacknode" + counter + ">");
                labelwr.append("\t" + object + "\n");
                counter++;
            }        
            triple = ntParser.line();
        }
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
