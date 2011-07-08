package sjtu.apex.gse.operator.join;

import java.util.ArrayList;
import java.util.List;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.operator.visitor.PlanVisitor;
import sjtu.apex.gse.operator.web.WebPatternPlan;
import sjtu.apex.gse.operator.web.WebPatternScan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class NestedLoopJoinPlan implements Plan {
	
	private Plan l, r;
	private List<Integer> li, ri;
	private QuerySchema sch;
	private QueryGraphNode rSubNode, rObjNode;
	private QuerySystem sys;
	
	/**
	 * 
	 * @param l
	 * @param r
	 * @param jn
	 * @param sch
	 */
	public NestedLoopJoinPlan(Plan l, Plan r, List<QueryGraphNode> jn, QuerySchema sch, QuerySystem sys) {
		if (!(r instanceof WebPatternPlan))
			throw new IllegalArgumentException();
		
		QuerySchema tl, tr;
		
		this.l = l;
		this.r = r;
		this.sch = sch;
		
		tl = l.getSchema();
		tr = r.getSchema();
		li = new ArrayList<Integer>(jn.size());
		ri = new ArrayList<Integer>(jn.size());
		for (int i = 0; i < jn.size(); i++) {
			li.add(tl.getNodeID(jn.get(i)));
			ri.add(tr.getNodeID(jn.get(i)));
		}
		
		QueryGraph qg = tr.getQueryGraph(); 
		
		if (qg.edgeCount() != 1)
			throw new IllegalArgumentException();
		
		rSubNode = qg.getEdge(0).getNodeFrom();
		rObjNode = qg.getEdge(0).getNodeTo();
		
		this.sys = sys;
	}
	
	public Plan leftPlan() {
		return l;
	}
	
	public Plan rightPlan() {
		return r;
	}

	@Override
	public Scan open() {
		return new NestedLoopJoinScan(l.open(), new WebPatternScan(r.getSchema(), sys.idManager(), sys.sourceManager(), sys.webRepository()), li, ri, sch, rSubNode, rObjNode);
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
		return l.diskIO() + r.diskIO() + resultCount();
	}

	@Override
	public int resultCount() {
		return Math.min(l.resultCount(), r.resultCount());
	}
	
	@Override
	public String toString() {
		return "NestedLoopJoinPlan(" + l.toString() + "," + r.toString() + ")";
	}

	@Override
	public int executionCost() {
		return diskIO() + webAccess() * 100;
	}

	@Override
	public int webAccess() {
		return l.webAccess() + resultCount() * 3;
	}

	@Override
	public void accept(PlanVisitor visitor) {
		visitor.visit(this);		
	}

}
