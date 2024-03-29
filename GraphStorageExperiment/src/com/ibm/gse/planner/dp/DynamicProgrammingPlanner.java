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
		QueryGraph graph = g.getQueryGraph();
		OptimalArray optArr = new OptimalArray(graph);
		List<PatternInfo> sbp = GraphStorage.patternMan.getSubPatterns(graph); 

		for (PatternInfo pi : sbp) {
			Set<String> pis = new HashSet<String>();
			
			Plan p = new PatternPlan(new QuerySchema(pi.getPattern(), pi.getCoveredNodes()));

			pis.add(pi.getPatternString());
			optArr.setInitValue(new OptimalArrayElem(p, pis));
		}

		Set<OptimalArrayElem> ext;
		while ((ext = optArr.nextStage()) != null) {
			for (OptimalArrayElem elem : ext) {
				Set<String> containedPattern = elem.getContainedPatterns();
				
				for (PatternInfo p : sbp)
					if (!containedPattern.contains(p.getPatternString()) && isExtendAndConnected(p, elem)){
						Set<String> con = getContainedPattern(elem.getContainedPatterns(), p.getPatternString());
						Plan pln = getJoinSelectPlan(elem, p, g);
						OptimalArrayElem oae = new OptimalArrayElem(pln, con);

						optArr.update(oae);
					}
			}
		}

		return optArr.getOptimalElem().getPlan();
	}

	/**
	 * 
	 * @param plan
	 * @param p
	 * @param g
	 * @return
	 */
	private Plan getJoinSelectPlan(OptimalArrayElem elem, PatternInfo p, QuerySchema g) {
//		Set<QueryGraphNode> nsn = g.getSelectedNodeSet();
		Plan plan = elem.getPlan();
		Set<QueryGraphNode> osn = plan.getSchema().getSelectedNodeSet();
		Set<QueryGraphEdge> nes = p.getCoveredEdges();
		Set<QueryGraphNode> nns = p.getConstrainedNodes();
		Set<QueryGraphEdge> oes = new HashSet<QueryGraphEdge>(elem.getCoveredEdges());
		Set<QueryGraphNode> ons = new HashSet<QueryGraphNode>(elem.getConstrainedNodes());
		Set<QueryGraphNode> notSat = getPlanSelectedNode(g, p.getCoveredNodes(), oes);

		Plan pp = new PatternPlan(new QuerySchema(p.getPattern(), notSat), p.getPatternString());

		List<QueryGraphNode> joinNode = new ArrayList<QueryGraphNode>();
		for (int i = 0; i < p.getPattern().nodeCount(); i++) {
			QueryGraphNode n = p.getPattern().getNode(i);
			if (osn.contains(n))
				joinNode.add(n);
		}

		oes.addAll(nes);
		ons.addAll(nns);
		QueryGraph ng = g.getQueryGraph().getInducedSubgraph(ons, oes);
		notSat = getPlanSelectedNode(g, ng.getNodeSet(), oes);

		pp = new MergeJoinPlan(pp, plan, joinNode, new QuerySchema(ng, notSat));

		return pp;
	}

	/**
	 * Check whether a sub-pattern extends an existing query graph coverage and the extended
	 * coverage remains connected
	 * @param subpattern The target sub-pattern
	 * @param coverage Edges contained within the coverage
	 */
	private boolean isExtendAndConnected(PatternInfo subpattern, OptimalArrayElem coverage) {
		Set<QueryGraphEdge> containedEdge = coverage.getCoveredEdges();
		Set<QueryGraphNode> containedNode = coverage.getCoveredNodes();
		Set<QueryGraphNode> constrainedNode = coverage.getConstrainedNodes();
		boolean extended = false, connected = false; 

		for (QueryGraphEdge e : subpattern.getCoveredEdges()) {
			
			if (!extended && !containedEdge.contains(e)) extended = true;
			
			if (extended) break;
		}
		
		for (QueryGraphNode n : subpattern.getCoveredNodes()) {
			
			if (!extended && !n.isGeneral() && !constrainedNode.contains(n)) extended = true;
			
			if (containedNode.contains(n)) connected = true;
			
		}
		
		return (extended & connected);
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
	 * @param patternNodeSet The set of nodes in the pattern
	 * @param patternEdgeSet The set of edges in the pattern
	 * @return
	 */
	private Set<QueryGraphNode> getPlanSelectedNode(QuerySchema qs, Set<QueryGraphNode> patternNodeSet, Set<QueryGraphEdge> patternEdgeSet) {
		QueryGraph graph = qs.getQueryGraph();
		Set<QueryGraphNode> result = new HashSet<QueryGraphNode>();

		
		for (QueryGraphNode n : patternNodeSet) {
				
				if (qs.hasNode(n)) {
					result.add(n);
					continue;
				}
				
				if (n.isGeneral() && !n.getAncestor().isGeneral())
					result.add(n);
				
				List<Connectivity> conns = n.getConnectivities();

				for (Connectivity c : conns)
					if (!patternEdgeSet.contains(c.getEdge())) {
						result.add(n);
						break;
					}
			}

		return result;
	}

}
