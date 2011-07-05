package sjtu.apex.gse.planner.dp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.pattern.PatternInfo;
import sjtu.apex.gse.planner.Planner;
import sjtu.apex.gse.planner.dp.factory.OperatorFactory;
import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

/**
 * A planner that applies dynamic programming algorithm
 * @author Tian Yuan
 *
 */
public class DynamicProgrammingPlanner implements Planner {
	
	private QuerySystem qs;
	private OperatorFactory opFac;
	private boolean strictPattern;
	
	public DynamicProgrammingPlanner(QuerySystem querySystem, OperatorFactory opFac, boolean strictPattern) {
		this.qs = querySystem;
		this.opFac = opFac;
		this.strictPattern = strictPattern;
	}

	@Override
	public Plan plan(QuerySchema g) {
		QueryGraph graph = g.getQueryGraph();
		OptimalArray optArr = new OptimalArray(graph);	
		List<PatternInfo> sbp = qs.patternManager().getSubPatterns(graph, !strictPattern); 

		for (PatternInfo pi : sbp) {
			Set<PatternInfo> pis = new HashSet<PatternInfo>();
			
			Plan p = opFac.getAtomicPlan(new QuerySchema(pi.getPattern(), pi.getCoveredNodes()), qs, pi.getSources());
			
			if (p != null) {
				pis.add(pi);
				optArr.setInitValue(new OptimalArrayElem(p, pis, null, pi));
			}
		}

		Set<OptimalArrayElem> ext;
		while ((ext = optArr.nextStage()) != null) {
			for (OptimalArrayElem elem : ext) {
				Set<PatternInfo> containedPattern = elem.getContainedPatterns();
				
				for (PatternInfo p : sbp)
					if (!containedPattern.contains(p) && isExtendAndConnected(p, elem)){
						Set<PatternInfo> con = getContainedPattern(elem.getContainedPatterns(), p);
						Plan pln = getJoinSelectPlan(elem, p, g);
						OptimalArrayElem oae = new OptimalArrayElem(pln, con, elem.getSatisfiedNodes(), p);

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
		Plan plan = elem.getPlan();
		Set<QueryGraphNode> osn = plan.getSchema().getSelectedNodeSet();
		Set<QueryGraphEdge> nes = p.getCoveredEdges();
		Set<QueryGraphNode> nns = p.getConstrainedNodes();
		Set<QueryGraphEdge> oes = new HashSet<QueryGraphEdge>(elem.getCoveredEdges());
		Set<QueryGraphNode> ons = new HashSet<QueryGraphNode>(elem.getSatisfiedNodes());
		Set<QueryGraphNode> notSat = getPlanSelectedNode(g, p.getCoveredNodes(), oes);

		Plan pp = opFac.getAtomicPlan(new QuerySchema(p.getPattern(), notSat), qs, p.getPatternString(), p.getSources());

		List<QueryGraphNode> joinNode = new ArrayList<QueryGraphNode>();
		for (int i = 0; i < p.getPattern().nodeCount(); i++) {
			QueryGraphNode n = p.getPattern().getNode(i);
			if (osn.contains(n))
				joinNode.add(n);
		}

		oes.addAll(nes);
		ons.addAll(nns);
		QueryGraph ng = g.getQueryGraph().getInducedSubgraph(ons, oes, null, null);
		notSat = getPlanSelectedNode(g, ng.getNodeSet(), oes);

		pp = opFac.getJoinPlan(plan, pp, joinNode, new QuerySchema(ng, notSat), qs, (p.getInstanceCount() == -1));

		return pp;
	}

	/**
	 * Check whether a sub-pattern extends an existing query graph coverage and the extended
	 * coverage remains connected
	 * @param subpattern - The target sub-pattern
	 * @param coverage - Edges contained within the coverage
	 */
	private boolean isExtendAndConnected(PatternInfo subpattern, OptimalArrayElem coverage) {
		Set<QueryGraphEdge> coveredEdge = coverage.getCoveredEdges();
		Set<QueryGraphNode> coveredNode = coverage.getCoveredNodes();
		Set<QueryGraphNode> satisfiedNode = coverage.getSatisfiedNodes();
		boolean extended = false, connected = false; 

		for (QueryGraphEdge e : subpattern.getCoveredEdges()) {
			
			if (!extended && !coveredEdge.contains(e)) extended = true;
			
			if (extended) break;
		}
		
		for (QueryGraphNode n : subpattern.getCoveredNodes()) {
			
			if (!extended && !n.isGeneralized() && !satisfiedNode.contains(n)) extended = true;
			
			if (coveredNode.contains(n)) connected = true;
			
		}
		
		return (extended & connected);
	}

	/**
	 * Get the set of patterns checked
	 * @param prev The set of checked patterns before;
	 * @param ps The newly checked pattern
	 */
	private Set<PatternInfo> getContainedPattern(Set<PatternInfo> prev, PatternInfo ps) {
		Set<PatternInfo> result = new HashSet<PatternInfo>(prev);
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
