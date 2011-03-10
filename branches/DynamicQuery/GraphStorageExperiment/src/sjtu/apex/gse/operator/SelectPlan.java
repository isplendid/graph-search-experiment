package sjtu.apex.gse.operator;

import sjtu.apex.gse.struct.QuerySchema;

/**
 * The node that selects specified nodes out of the result
 * @author Tian Yuan
 *
 */
public class SelectPlan implements Plan {
	
	Plan src;
	QuerySchema sch;
	
	public SelectPlan(Plan src, QuerySchema sch) {
		this.src = src;
		this.sch = sch;
	}
	
	public Scan open() {
		return src.open();
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public QuerySchema getSchema() {
		return sch;
	}

	@Override
	public int diskIO() {
		return src.diskIO();
	}

	/**
	 * This is a rough estimation
	 */
	@Override
	public int resultCount() {
		return src.resultCount();
	}

	@Override
	public int executionCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int webAccess() {
		// TODO Auto-generated method stub
		return 0;
	}


}
