package sjtu.apex.gse.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.pattern.PatternInfo;
import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;

/**
 * 
 * @author Tian Yuan
 *
 */
public class PatternManager {

	PatternCodec codec;
	IndexManager indexMan;

	public PatternManager(PatternCodec codec, IndexManager indexMan) {
		this.codec = codec;
		this.indexMan = indexMan;
	}

	/**
	 * Get the number of instances according with the given pattern (by structural graph)
	 * @param graph The structural 
	 * @return The number of instances
	 */
	public Integer getPatternInstanceCount(QueryGraph graph) {
		return getPatternInstanceCount(codec.encodePattern(graph), graph.nodeCount());
	}

	/**
	 * Get the number of instances according with the given pattern (by encoded string)
	 * @param graph - The structural 
	 * @return The number of instances
	 */
	public Integer getPatternInstanceCount(String ps, int size) {
		int res = indexMan.getPatternCount(ps, size);
		if (res == -1)
			return null;
		else
			return res;
	}
	
	/**
	 * Get the list of relevant sources for a pattern
	 * @param ps - The encoded pattern string
	 * @param size - The number of nodes in the pattern
	 * @return The list of sources.
	 */
	public Set<Integer> getPatternRelevantSources(String ps, int size) {
		return indexMan.getSourceList(ps, size);
	}
	
	private void addTriplePatterns(SubpatternSet generated, List<PatternInfo> elems, QueryGraph graph, boolean returnTriple) {
		for (int i = 0; i < graph.edgeCount(); i++) {
			QueryGraphEdge e = graph.getEdge(i);
			Set<QueryGraphEdge> es = new HashSet<QueryGraphEdge>();
			
			es.add(e);
			
			for (int j = 0; j < 4; j++) {
				Set<QueryGraphNode> ns = new HashSet<QueryGraphNode>();
				if ((j & 1) == 1) ns.add(e.getNodeFrom());
				if ((j & 2) == 2) ns.add(e.getNodeTo());
				
				QueryGraph g = graph.getInducedSubgraph(ns, es);
				String ps = codec.encodePattern(g);
				
				Integer insCnt = getPatternInstanceCount(ps, g.nodeCount());
				
				if (insCnt != null || (returnTriple && j == 3)) {
					if (generated.get(ps, g.getEdgeSet(), g.getNodeSet()) == null) {
						PatternInfo pi = new PatternInfo(g, ps, insCnt, getPatternRelevantSources(ps, g.nodeCount()), null);
						elems.add(pi);
						generated.add(pi);
					}
				}
			}
			
		}
	}
	
	private void contrainSources(PatternInfo subject, Set<Integer> sources) {
		subject.getSources().retainAll(sources);		
	}
	
	private boolean updateGraphs(QueryGraph ng, SubpatternSet generated, List<PatternInfo> elems, PatternInfo parent) {
		boolean ret = false;
		String ps = codec.encodePattern(ng);
		Integer insCnt;
		
		if ((insCnt = getPatternInstanceCount(ps, ng.nodeCount())) != null) {
			Set<Integer> tmpSrc = new HashSet<Integer>(parent.getSources());
			tmpSrc.retainAll(getPatternRelevantSources(ps, ng.nodeCount()));
			
			PatternInfo pi;
			if ((pi = generated.get(ps, ng.getEdgeSet(), ng.getNodeSet())) == null) {
				pi = new PatternInfo(ng, ps, insCnt, tmpSrc, parent);
				elems.add(pi);
				generated.add(pi);
			} else {
				PatternInfo itr = parent;
				do {
					contrainSources(itr, tmpSrc);
					itr = itr.getParent();
				} while (itr != null);
			}
			ret = true;
		}
		
		return ret;
	}
	
	private void addComplexPatterns(SubpatternSet generated, List<PatternInfo> elems, QueryGraph graph) {
		PatternInfo toExt;
		int pointer = 0;
		
		while (pointer < elems.size()) {
			toExt = elems.get(pointer);
			Set<QueryGraphNode> nodeCovered = toExt.getCoveredNodes();
			Set<QueryGraphEdge> edgeCovered = toExt.getCoveredEdges();
			Set<QueryGraphNode> nodeConstrained = toExt.getConstrainedNodes();
			
			for (QueryGraphNode n : nodeCovered) {
				//Extend the pattern by adding edges
				for (Connectivity c : n.getAncestor().getConnectivities()) 
					if (!edgeCovered.contains(c.getEdge())){
						edgeCovered.add(c.getEdge());
						nodeConstrained.add(n.getAncestor());
						
						QueryGraph ng = graph.getInducedSubgraph(nodeConstrained, edgeCovered);
						boolean updated = updateGraphs(ng, generated, elems, toExt);
						
						nodeConstrained.remove(n.getAncestor());
						
						if (!updated) {
							ng = graph.getInducedSubgraph(nodeConstrained, edgeCovered);
							updateGraphs(ng, generated, elems, toExt);
						}
						edgeCovered.remove(c.getEdge());
					}
				
				//Extend the pattern by adding constraints
				if (n.isGeneral() && n.isGeneralized()) {
					nodeConstrained.add(n);
					
					QueryGraph ng = graph.getInducedSubgraph(nodeConstrained, edgeCovered);
					updateGraphs(ng, generated, elems, toExt);
					
					nodeConstrained.remove(n);
				}
			}
			
			pointer++;
		}
	}

	/**
	 * Get all legal sub-patterns of a given pattern. BFS search algorithm is applied here
	 * @param graph - The given pattern
	 * @param returnTriple - Indicates whether return at least a triple pattern for each edge regardless of whether it is indexed  
	 * @return A set of legal sub-patterns
	 */
	public List<PatternInfo> getSubPatterns(QueryGraph graph, boolean returnTriple) {
		List<PatternInfo> elems = new ArrayList<PatternInfo>();
		SubpatternSet generated = new SubpatternSet();
		
		//Triple patterns are definitely added as candidates
		addTriplePatterns(generated, elems, graph, returnTriple);

		//Complex patterns are added if exist in index
		addComplexPatterns(generated, elems, graph);
		
		return elems;
	}

	/**
	 * Get the pattern codec
	 */
	public PatternCodec getCodec() {
		return codec;
	}
}
