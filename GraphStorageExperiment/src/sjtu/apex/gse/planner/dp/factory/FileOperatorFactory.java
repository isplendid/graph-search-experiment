package sjtu.apex.gse.planner.dp.factory;

import java.util.List;
import java.util.Set;

import sjtu.apex.gse.operator.PatternPlan;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.join.MergeJoinPlan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class FileOperatorFactory extends OperatorFactory {

	@Override
	public Plan getAtomicPlan(QuerySchema sch, QuerySystem sys, String ptrStr, Set<Integer> sources) {
		return new PatternPlan(sch, sys, ptrStr);
	}

	@Override
	public Plan getJoinPlan(Plan left, Plan right,
			List<QueryGraphNode> joinNode, QuerySchema sch, QuerySystem qs,
			boolean bottomUp) {
		return new MergeJoinPlan(left, right, joinNode, sch);
	}

}
