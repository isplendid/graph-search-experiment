package sjtu.apex.gse.operator.web;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class WebPatternPlan implements Plan {
	
	public WebPatternPlan(QuerySchema sch, QuerySystem qs, String patternStr) {
		
	}

	@Override
	public Scan open() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public QuerySchema getSchema() {
		// TODO Auto-generated method stub
		return null;
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
