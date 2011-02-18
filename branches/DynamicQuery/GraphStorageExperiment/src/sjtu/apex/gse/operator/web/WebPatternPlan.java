package sjtu.apex.gse.operator.web;

import java.util.HashSet;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class WebPatternPlan implements Plan {
	QuerySystem qs;
	QuerySchema sch;
	
	public WebPatternPlan(QuerySchema sch, QuerySystem qs) {
		this.sch = sch;
		this.qs = qs;
	}

	@Override
	public Scan open() {
		WebPatternScan wps = new WebPatternScan(sch, qs.idManager(), qs.sourceManager());
		
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int resultCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
