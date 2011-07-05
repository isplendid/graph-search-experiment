package sjtu.apex.gse.planner.dp.factory;

import java.util.List;
import java.util.Set;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.join.HashJoinPlan;
import sjtu.apex.gse.operator.join.NestedLoopJoinPlan;
import sjtu.apex.gse.operator.web.WebPatternPlan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class WebOperatorFactory extends OperatorFactory {

	@Override
	public Plan getAtomicPlan(QuerySchema sch, QuerySystem sys, String ptrStr, Set<Integer> sources) {
		if (sch.getQueryGraph().edgeCount() != 1)
			return null;
		else
			return new WebPatternPlan(sch, sys, sources);
	}

	@Override
	public Plan getJoinPlan(Plan left, Plan right,
			List<QueryGraphNode> joinNode, QuerySchema sch, QuerySystem qs,
			boolean bottomUp) {
		Plan ret;
		
		if (bottomUp) {
			ret = new NestedLoopJoinPlan(left, right, joinNode, sch, qs);
		}
		else {
			ret = new HashJoinPlan(left, right, joinNode, sch);
		}
		
		return ret;
	}

}
