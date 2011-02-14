package sjtu.apex.gse.operator.join;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QuerySchema;

public class NestedLoopJoinPlan implements Plan {

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
