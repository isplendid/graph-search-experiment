package sjtu.apex.gse.operator;

import java.util.Map;

import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;


public class PatternPlan implements Plan {
	
	QuerySchema sch;
	QuerySystem qs;
	Map<QueryGraphNode, Integer> cmap;
	String ps;
	private int rcCache = -1;
	
	public PatternPlan(QuerySchema sch, QuerySystem qs) {
		this(sch, qs, qs.patternManager().getCodec().encodePattern(sch.getQueryGraph()));
	}
	
	public PatternPlan(QuerySchema sch, QuerySystem qs, String ps) {
		this.sch = sch;
		this.ps = ps;
		this.qs = qs;
		cmap = qs.columnNodeMap().getMap(sch.getQueryGraph());
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public Scan open() {
		return new PatternScan(sch, qs, cmap, ps);
	}

	@Override
	public int diskIO() {
		return resultCount();
	}

	@Override
	public int resultCount() {
		if (rcCache != -1)
			rcCache = qs.patternManager().getPatternInstanceCount(ps, sch.getQueryGraph().nodeCount());
		
		return rcCache;
	}
	
	@Override
	public String toString() {
		return "PatternPlan(" + ps + ")";
	}

	@Override
	public int executionCost() {
		return diskIO();
	}

	@Override
	public int webAccess() {
		return 0;
	}

}
