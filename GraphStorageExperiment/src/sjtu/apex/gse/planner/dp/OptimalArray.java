package sjtu.apex.gse.planner.dp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;


/**
 * An optimal array that stores the optimal value of each states, used in DP
 * algorithm
 * @author Tian Yuan
 *
 */
class OptimalArray {
	
	int p2[], numEdge = 1;
	OptimalArrayElem encode[];
	OptimalArrayEvaluator eval = new OptimalArrayEvaluator();
	Map<QueryGraphEdge, Integer> eMap = new HashMap<QueryGraphEdge, Integer>();
	Map<QueryGraphNode, Integer> nMap = new HashMap<QueryGraphNode, Integer>();
	Set<OptimalArrayElem>[] pending;
	
	private void initP2(int size) {
		p2 = new int[size + 1];
		
		p2[0] = 1;
		for (int i = 1; i < p2.length; i++)
			p2[i] = p2[i - 1] << 1;
	}
	
	private int encodeElem(OptimalArrayElem value) {
		int total = 0;
		
		for (QueryGraphEdge e : value.getCoveredEdges())
			total += p2[eMap.get(e)];
		
		for (QueryGraphNode n : value.getSatisfiedNodes())
			total += p2[nMap.get(n)];
		
		return total;
	}
	
	OptimalArray(QueryGraph g) {
		Set<QueryGraphNode> nodes = g.getConstrainedNodeSet();
		
		 initP2(g.edgeCount() + nodes.size() + 1);
		 
		 int serial = 0;
		 
		 for (int i = 0; i < g.edgeCount(); i++)
			 eMap.put(g.getEdge(i), serial ++);
		 
		 for (QueryGraphNode n : nodes)
			 nMap.put(n, serial ++);
		 
		 pending = new Set[g.edgeCount() + nodes.size() + 1];
		 
		 for (int i = 0; i < pending.length; i++)
			 pending[i] = new HashSet<OptimalArrayElem>();
		 
		 encode = new OptimalArrayElem[p2[g.edgeCount() + nodes.size()]];
	}

	/**
	 * Set the initial states 
	 * @param value The initial states
	 */
	void setInitValue(OptimalArrayElem value) {
		if (numEdge == 1 && value.getContainedPatterns().size() == 1) {
			int code = encodeElem(value);
			
			if (encode[code] == null || eval.evaluate(encode[code]) > eval.evaluate(value)) {
				encode[code] = value;
				pending[value.getCoveredEdges().size() + value.getSatisfiedNodes().size()].add(value);
			}
		}
	}
	
	/**
	 * Advance to next stage
	 * @return The states to be expanded
	 */
	Set<OptimalArrayElem> nextStage() {
		numEdge ++;
		if (numEdge > pending.length) 
			return null;
		else
			return pending[numEdge - 1];
	}
	
	/**
	 * Update the optimal value of the given state
	 * @param value The given state
	 */
	void update(OptimalArrayElem value) {
		int enc = encodeElem(value);
		
		System.out.println(value.getPlan() + " : " + value.getPlan().executionCost());
		
		if (encode[enc] == null || eval.evaluate(value) < eval.evaluate(encode[enc])) {
			pending[value.getCoveredEdges().size() + value.getSatisfiedNodes().size()].remove(encode[enc]);
			pending[value.getCoveredEdges().size() + value.getSatisfiedNodes().size()].add(value);
			encode[enc] = value;
		}
	}
	
	/**
	 * Get the final optimal plan that covers all nodes
	 */
	OptimalArrayElem getOptimalElem() {
		return encode[encode.length - 1];
	}
}
