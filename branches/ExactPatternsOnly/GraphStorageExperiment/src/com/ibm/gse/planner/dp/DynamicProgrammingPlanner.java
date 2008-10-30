package com.ibm.gse.planner.dp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.gse.pattern.PatternInfo;
import com.ibm.gse.planner.Planner;
import com.ibm.gse.query.MergeJoinPlan;
import com.ibm.gse.query.PatternPlan;
import com.ibm.gse.query.Plan;
import com.ibm.gse.struct.Connectivity;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;
import com.ibm.gse.system.GraphStorage;

/**
 * A planner that applies dynamic programming algorithm
 * @author Tian Yuan
 *
 */
public class DynamicProgrammingPlanner implements Planner {

	@Override
	public Plan plan(QuerySchema g) {
		//		int prune = Integer.MAX_VALUE;
		QueryGraph graph = g.getQueryGraph();
		OptimalArray optArr = new OptimalArray(graph);
		List<PatternInfo> sbp = GraphStorage.patternMan.getSubPatterns(graph); 

		for (PatternInfo pi : sbp) 
			if (pi.getCoveredNodes().size() > 1){
				Set<String> pis = new HashSet<String>();

				Plan p = new PatternPlan(new QuerySchema(pi.getPattern(), pi.getCoveredNodes()));

				pis.add(pi.getPatternString());
				optArr.setInitValue(new OptimalArrayElem(p, pis));
			}

		Set<OptimalArrayElem> ext;
		while ((ext = optArr.nextStage()) != null) {
			for (OptimalArrayElem elem : ext)
				for (PatternInfo p : sbp) 
					if (!elem.getContainedPatterns().contains(p.getPatternString()) && isExtendAndConnected(p.getPattern(), elem)){
						Set<String> con = getContainedPattern(elem.getContainedPatterns(), p.getPatternString());
						Plan pln = getJoinSelectPlan(elem.getPlan(), p, g);
						OptimalArrayElem oae = new OptimalArrayElem(pln, con);

						optArr.update(oae);
					}
		}

		return optArr.getOptimalElem().getPlan();
	}

	private Plan getJoinSelectPlan(Plan plan, PatternInfo p, QuerySchema g) {
//		Set<QueryGraphNode> nsn = g.getSelectedNodeSet();
		Set<QueryGraphNode> osn = plan.getSchema().getSelectedNodeSet();
		Set<QueryGraphEdge> nes = p.getCoveredEdges();
		Set<QueryGraphEdge> oes = plan.getSchema().getQueryGraph().getEdgeSet();
		Set<QueryGraphNode> notSat = getPlanSelectedNode(g, p.getCoveredNodes(), nes);

		Plan pp = new PatternPlan(new QuerySchema(p.getPattern(), notSat), p.getPatternString());

		List<QueryGraphNode> joinNode = new ArrayList<QueryGraphNode>();
		for (int i = 0; i < p.getPattern().nodeCount(); i++) {
			QueryGraphNode n = p.getPattern().getNode(i);
			if (osn.contains(n))
				joinNode.add(n);
		}

		oes.addAll(nes);
		QueryGraph ng = g.getQueryGraph().getEdgeInducedSubgraph(oes);
		notSat = getPlanSelectedNode(g, ng.getNodeSet(), oes);
//		notSat.addAll(nsn);

		pp = new MergeJoinPlan(pp, plan, joinNode, new QuerySchema(ng, notSat));

		return pp;
	}

	/**
	 * Check whether a sub-pattern extends an existing query graph coverage and the extended
	 * coverage remains connected
	 * @param subpattern The target sub-pattern
	 * @param coverage Edges contained within the coverage
	 */
	private boolean isExtendAndConnected(QueryGraph subpattern, OptimalArrayElem coverage) {
		Set<QueryGraphEdge> containedEdge = coverage.getCoveredEdges();
		Set<QueryGraphNode> containedNode = coverage.getCoveredNodes();
		boolean extended = false, connected = false; 

		for (int i = 0; i < subpattern.edgeCount(); i++) {
			QueryGraphEdge edge = subpattern.getEdge(i);

			if (!extended && !containedEdge.contains(edge)) extended = true;
			if (!connected && (containedNode.contains(edge.getNodeTo()) 
					|| containedNode.contains(edge.getNodeFrom()))) connected = true;
			if (connected && extended) return true;
		}
		return false;
	}

	/**
	 * Get the set of patterns checked
	 * @param prev The set of checked patterns before;
	 * @param ps The newly checked pattern
	 */
	private Set<String> getContainedPattern(Set<String> prev, String ps) {
		Set<String> result = new HashSet<String>(prev);
		result.add(ps);
		return result;
	}

	/**
	 * Decide the nodes to retrieve out of the pattern
	 * 
	 * Notes : Two kinds of nodes must be retrieved, one of which are nodes that are selected
	 * as result and the other of which are the ones to be joined with other nodes
	 * 
	 * @param qs The global (user given) query schema
	 * @param ns The set of nodes in the pattern
	 * @param ce The set of edges already covered by existing plan
	 * @return
	 */
	private Set<QueryGraphNode> getPlanSelectedNode(QuerySchema qs, Set<QueryGraphNode> ns, Set<QueryGraphEdge> ce) {
		QueryGraph graph = qs.getQueryGraph();
		Set<QueryGraphNode> result = new HashSet<QueryGraphNode>();

		for (int i = 0; i < graph.nodeCount(); i++)
			if (ns.contains(graph.getNode(i))){
				QueryGraphNode n = graph.getNode(i);
				
				if (qs.hasNode(n)) {
					result.add(n);
					continue;
				}
				
				List<Connectivity> conns = n.getConnectivities();

				for (Connectivity c : conns)
					if (!ce.contains(c.getEdge())) {
						result.add(n);
						break;
					}
			}

		return result;
	}

}
