package com.ibm.gse.query;

import java.util.Map;

import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;
import com.ibm.gse.system.GraphStorage;

public class PatternPlan implements Plan {
	
	QuerySchema sch;
	Map<QueryGraphNode, Integer> cmap;
	String ps;
	private int rcCache = -1;
	
	public PatternPlan(QuerySchema sch) {
		this(sch, GraphStorage.patternMan.getCodec().encodePattern(sch.getQueryGraph()));
	}
	
	public PatternPlan(QuerySchema sch, String ps) {
		this.sch = sch;
		this.ps = ps;
		cmap = GraphStorage.columnNodeMap.getMap(sch.getQueryGraph());
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public Scan open() {
		return new PatternScan(sch, cmap, ps);
	}

	@Override
	public int diskIO() {
		return resultCount();
	}

	@Override
	public int resultCount() {
		if (rcCache == -1)
			rcCache = GraphStorage.patternMan.getPatternInstanceCount(ps, sch.getQueryGraph().nodeCount());
		
		return rcCache;
	}

}
