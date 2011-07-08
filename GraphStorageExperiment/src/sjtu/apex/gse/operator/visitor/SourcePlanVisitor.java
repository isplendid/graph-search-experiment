package sjtu.apex.gse.operator.visitor;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.operator.PatternPlan;
import sjtu.apex.gse.operator.SelectPlan;
import sjtu.apex.gse.operator.SortPlan;
import sjtu.apex.gse.operator.join.HashJoinPlan;
import sjtu.apex.gse.operator.join.MergeJoinPlan;
import sjtu.apex.gse.operator.join.NestedLoopJoinPlan;
import sjtu.apex.gse.operator.web.WebPatternPlan;

public class SourcePlanVisitor implements PlanVisitor {
	
	Set<Integer> sourceSet;
	
	public SourcePlanVisitor() {
		sourceSet = new HashSet<Integer>();
	}
	
	public Set<Integer> getSourceSet() {
		return sourceSet;
	}

	@Override
	public void visit(MergeJoinPlan plan) {
		plan.leftPlan().accept(this);
		plan.rightPlan().accept(this);
	}

	@Override
	public void visit(NestedLoopJoinPlan plan) {
		plan.leftPlan().accept(this);
		plan.rightPlan().accept(this);
	}

	@Override
	public void visit(WebPatternPlan plan) {
		sourceSet.addAll(plan.getPlanSource());
	}

	@Override
	public void visit(HashJoinPlan plan) {
		plan.leftPlan().accept(this);
		plan.rightPlan().accept(this);
	}

	@Override
	public void visit(PatternPlan plan) {
		
	}

	@Override
	public void visit(SortPlan plan) {
		plan.sourcePlan().accept(this);
	}

	@Override
	public void visit(SelectPlan plan) {
		
	}

}
