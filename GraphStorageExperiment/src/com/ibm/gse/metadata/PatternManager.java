package com.ibm.gse.metadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.gse.pattern.PatternCodec;
import com.ibm.gse.pattern.PatternInfo;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.system.ConnectionFactory;
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
		return GraphStorage.indexMan.getPatternCount(ps, size);
		//		try {
		//			Statement stm = conn.createStatement();
		//			ResultSet rs = stm.executeQuery("SELECT CNT FROM PATTERN WHERE P = '" + ps + "'");

		//			return rs.getInt(1); 
		//		} catch (SQLException e) {
		//			return null;
		//		}
	}

	/**
	 * Get all legal sub-patterns of the given pattern
	 * BFS search algorithm is applied here
	 * TODO This method should be revised
	 * @param graph The given pattern
	 * @return A set of legal sub-patterns
	 */
	public List<PatternInfo> getSubPatterns(QueryGraph graph) {
		List<PatternInfo> elems = new ArrayList<PatternInfo>();
		Set<String> generated = new HashSet<String>();
		int pointer = 0;
		//		Iterator<PatternInfo> itr = elems.iterator();
		PatternInfo toExt;

		for (int i = 0; i < graph.nodeCount(); i++) {
			QueryGraphNode n = graph.getNode(i);
			Set<QueryGraphNode> es = new HashSet<QueryGraphNode>();

			es.add(n);
			QueryGraph g = graph.getNodeInducedSubgraph(es);
			String ps = codec.encodePattern(g);
			elems.add(new PatternInfo(g, ps, new HashSet<QueryGraphEdge>(), getPatternInstanceCount(ps, g.nodeCount())));
			generated.add(ps);
		}

		Set<QueryGraphEdge> allEdges = graph.getEdgeSet();

		while (pointer < elems.size()) {
			toExt = elems.get(pointer);
			Set<QueryGraphEdge> es = null;

			for (QueryGraphEdge qe : allEdges)
				if (!toExt.getCoveredEdges().contains(qe) && 
						(toExt.getCoveredNodes().contains(qe.getNodeFrom()) ^ toExt.getCoveredNodes().contains(qe.getNodeTo()))) {
					if (es == null) 
						es = new HashSet<QueryGraphEdge>(toExt.getCoveredEdges());
					es.add(qe);
					QueryGraph g = graph.getEdgeInducedSubgraph(es);
					String ps = codec.encodePattern(g);
					Integer insCnt;

					if (!generated.contains(ps) && (insCnt = getPatternInstanceCount(ps, g.nodeCount())) != null) {
						elems.add(new PatternInfo(g, ps, es, insCnt));
						generated.add(ps);
						es = null;
					}
					else
						es.remove(qe);
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
