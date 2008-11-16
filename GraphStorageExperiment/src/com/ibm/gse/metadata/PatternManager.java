package com.ibm.gse.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.gse.pattern.PatternCodec;
import com.ibm.gse.pattern.PatternInfo;
import com.ibm.gse.struct.Connectivity;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.system.GraphStorage;

/**
 * 
 * @author Tian Yuan
 *
 */
public class PatternManager {

	//	static Connection conn = ConnectionFactory.createConnection();
	PatternCodec codec;

	public PatternManager(PatternCodec codec) {
		this.codec = codec;
	}

	/**
	 * Get the number of instances according with the given pattern (by structural graph)
	 * @param graph The structural 
	 * @return The number of instances
	 */
	public Integer getPatternInstanceCount(QueryGraph graph) {
		String pattern = codec.encodePattern(graph);

		return GraphStorage.indexMan.getPatternCount(pattern, graph.nodeCount());
		//		return getPatternInstanceCount(pattern);
	}

	/**
	 * Get the number of instances according with the given pattern (by encoded string)
	 * @param graph The structural 
	 * @return The number of instances
	 */
	public Integer getPatternInstanceCount(String ps, int size) {
		int res = GraphStorage.indexMan.getPatternCount(ps, size);
		if (res == -1)
			return null;
		else
			return res;
		//		try {
		//			Statement stm = conn.createStatement();
		//			ResultSet rs = stm.executeQuery("SELECT CNT FROM PATTERN WHERE P = '" + ps + "'");

		//			return rs.getInt(1); 
		//		} catch (SQLException e) {
		//			return null;
		//		}
	}

	/**
	 * Get all legal sub-patterns of the given pattern BFS search algorithm is applied here
	 * @param graph The given pattern
	 * @return A set of legal sub-patterns
	 */
	public List<PatternInfo> getSubPatterns(QueryGraph graph) {
		List<PatternInfo> elems = new ArrayList<PatternInfo>();
		Set<String> generated = new HashSet<String>();
		int pointer = 0;
		PatternInfo toExt;

		for (int i = 0; i < graph.nodeCount(); i++) {
			QueryGraphNode n = graph.getNode(i);
			Set<QueryGraphNode> ns = new HashSet<QueryGraphNode>();

			ns.add(n);
			QueryGraph g = graph.getInducedSubgraph(ns, null);
			String ps = codec.encodePattern(g);
			elems.add(new PatternInfo(g, ps, getPatternInstanceCount(ps, g.nodeCount())));
			generated.add(ps);
		}
		
		for (int i = 0; i < graph.edgeCount(); i++) {
			QueryGraphEdge e = graph.getEdge(i);
			Set<QueryGraphEdge> es = new HashSet<QueryGraphEdge>();
			
			es.add(e);
			QueryGraph g = graph.getInducedSubgraph(null, es);
			String ps = codec.encodePattern(g);
			elems.add(new PatternInfo(g, ps, getPatternInstanceCount(ps, g.nodeCount())));
			generated.add(ps);
		}

		while (pointer < elems.size()) {
			toExt = elems.get(pointer);
			Set<QueryGraphNode> nodeContained = toExt.getCoveredNodes();
			Set<QueryGraphEdge> edgeContained = toExt.getCoveredEdges();
			
			for (QueryGraphNode n : nodeContained) {
				for (Connectivity c : n.getAncestor().getConnectivities()) 
					if (!edgeContained.contains(c.getEdge())){
						edgeContained.add(c.getEdge());
						
						QueryGraph ng = graph.getInducedSubgraph(nodeContained, edgeContained);
						String ps = codec.encodePattern(ng);
						Integer insCnt;
						
						if (!generated.contains(ps) && (insCnt = getPatternInstanceCount(ps, ng.nodeCount())) != null) {
							elems.add(new PatternInfo(ng, ps, insCnt));
							generated.add(ps);
						}
						
						edgeContained.remove(c.getEdge());
					}
				
				if (n.isGeneral()) {
					Set<QueryGraphNode> nodeConstrained = toExt.getConstrainedNodes();
					nodeConstrained.add(n);
					
					QueryGraph ng = graph.getInducedSubgraph(nodeConstrained, edgeContained);
					String ps = codec.encodePattern(ng);
					Integer insCnt;
					
					if (!generated.contains(ps) && (insCnt = getPatternInstanceCount(ps, ng.nodeCount())) != null) {
						elems.add(new PatternInfo(ng, ps, insCnt));
						generated.add(ps);
					}
					
					nodeConstrained.remove(n);
				}
			}

			pointer++;
		}

		return elems;
	}

	/**
	 * Get the pattern codec
	 */
	public PatternCodec getCodec() {
		return codec;
	}
}
