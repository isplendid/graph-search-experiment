package sjtu.apex.gse.operator.visitor;

import sjtu.apex.gse.operator.PatternPlan;
import sjtu.apex.gse.operator.SelectPlan;
import sjtu.apex.gse.operator.SortPlan;
import sjtu.apex.gse.operator.join.HashJoinPlan;
import sjtu.apex.gse.operator.join.MergeJoinPlan;
import sjtu.apex.gse.operator.join.NestedLoopJoinPlan;
import sjtu.apex.gse.operator.web.WebPatternPlan;

public interface PlanVisitor {
	public void visit(MergeJoinPlan plan);
	public void visit(NestedLoopJoinPlan plan);
	public void visit(WebPatternPlan plan);
	public void visit(HashJoinPlan plan);
	public void visit(PatternPlan plan);
	public void visit(SortPlan plan);
	public void visit(SelectPlan plan);
}
