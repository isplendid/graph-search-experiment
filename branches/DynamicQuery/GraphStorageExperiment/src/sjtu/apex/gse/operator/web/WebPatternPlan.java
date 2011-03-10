package sjtu.apex.gse.operator.web;

import java.util.HashSet;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class WebPatternPlan implements Plan {
	private QuerySystem qs;
	private QuerySchema sch;
	private String ps;
	private Integer webAccCnt = null;
	private Integer resCnt = null;
	
	public WebPatternPlan(QuerySchema sch, QuerySystem qs) {
		this(sch, qs, qs.patternManager().getCodec().encodePattern(sch.getQueryGraph()));	
	}
	
	public WebPatternPlan(QuerySchema sch, QuerySystem qs, String ps) {
		this.sch = sch;
		this.qs = qs;
		this.ps = ps;
	}

	@Override
	public Scan open() {
		WebPatternScan wps = new WebPatternScan(sch, qs.idManager(), qs.sourceManager(), qs.webRepository());
		
		wps.addKey(-1, -1, new HashSet<Integer>());
		wps.keyEnded();
		
		return wps;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public int diskIO() {
		return 0;
	}

	@Override
	public int resultCount() {
		if (resCnt == null) {
			if (sch.getQueryGraph().getEdge(0).getNodeFrom().isGeneral() && sch.getQueryGraph().getEdge(0).getNodeTo().isGeneral())
				resCnt = 1000;
			else
				resCnt = 10;
		}
		
		return resCnt;
	}

	@Override
	public int executionCost() {
		return webAccess() * 100;
	}
	
	@Override
	public int webAccess() {
		if (webAccCnt == null) {
			if (sch.getQueryGraph().getEdge(0).getNodeFrom().isGeneral() && sch.getQueryGraph().getEdge(0).getNodeTo().isGeneral())
				webAccCnt = 20;
			else
				webAccCnt = 3;
		}
		
		return webAccCnt;
	}
	
	@Override
	public String toString() {
		return "WebPattern(" + ps + ")";
	}


}

