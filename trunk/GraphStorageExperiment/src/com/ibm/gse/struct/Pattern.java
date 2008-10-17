/**
 * <copyright> 
 * 
 * Copyright (c) 2007-2008 IBM Corporation and others. 
 * All rights reserved. 
 * Project name: GraphStroageExperiment
 * </copyright> 
 * 
 * $ Pattern.java, created: Sep 2, 2008 3:55:58 PM, author: niyuan $
 */

package com.ibm.gse.struct;

public class Pattern implements Comparable<Pattern> {

    String patternCode;
    int patternStat;
    
    public Pattern(String patterncode, int patternstat) {
        this.patternCode = patterncode;
        this.patternStat = patternstat;
    }
    
    public String getPatternCode() {
        return patternCode;
    }
    
    public int getPatternStat() {
        return patternStat;
    }
    
    @Override
    public int compareTo(Pattern other) {
        // TODO Auto-generated method stub
        return -Double.compare(patternStat, other.getPatternStat());
    }
}
