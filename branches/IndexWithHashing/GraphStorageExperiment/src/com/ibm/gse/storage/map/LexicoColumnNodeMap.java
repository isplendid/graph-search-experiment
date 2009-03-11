package com.ibm.gse.storage.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphNode;

/**
 * This class applies lexicographic to implement ColumnNodeMap
 * @author Tian Yuan
 *
 */
public class LexicoColumnNodeMap implements ColumnNodeMap {

	@Override
	public Map<QueryGraphNode, Integer> getMap(QueryGraph graph) {
		Map<QueryGraphNode, Integer> map = new HashMap<QueryGraphNode, Integer>();
		List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>(graph.getNodeSet());
		Collections.sort(nl, new LexicoComparator());
		
		for (int i = 0; i < nl.size(); i++)
			map.put(nl.get(i), i);
		
		return map;
	}

	class LexicoComparator implements Comparator<QueryGraphNode> {

		@Override
		public int compare(QueryGraphNode arg0, QueryGraphNode arg1) {
			int cmp;
			if ((cmp = arg0.getLabel().compareTo(arg1.getLabel())) != 0)
				return cmp;
			else if ((cmp = arg1.getOutDegree() - arg0.getOutDegree()) != 0)
				return cmp;
			else if ((cmp = arg1.getInDegree() - arg0.getInDegree()) != 0)
				return cmp;
			else
				return 0;
		}
		
	}
}
