package sjtu.apex.gse.planner.dp.factory;

import java.util.List;
import java.util.Set;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public abstract class OperatorFactory {
	
	/**
	 * 
	 * @param sch
	 * @param sys
	 * @return
	 */
	public Plan getAtomicPlan(QuerySchema sch, QuerySystem sys, Set<Integer> sources) {
		return getAtomicPlan(sch, sys, sys.patternCodec().encodePattern(sch.getQueryGraph()), sources);
	}
	
	/**
	 * 
	 * @param sch
	 * @param sys
	 * @param ptrStr
	 * @return
	 */
	public abstract Plan getAtomicPlan(QuerySchema sch, QuerySystem sys, String ptrStr, Set<Integer> sources);
	
	/**
	 * 
	 * @param left
	 * @param right
	 * @param joinNode
	 * @param sch
	 * @param qs
	 * @param bottomUp
	 * @return
	 */
	public abstract Plan getJoinPlan(Plan left, Plan right, List<QueryGraphNode> joinNode, QuerySchema sch, QuerySystem qs, boolean bottomUp);
}
