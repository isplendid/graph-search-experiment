package sjtu.apex.gse.pattern;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;


/**
 * This class applies pre-order string of a tree to encode a pattern Patterns
 * are restricted to tree patterns
 * 
 * @author Tian Yuan
 * 
 */
public class HashingPatternCodec implements PatternCodec {
	
	final String wildcard = "*";
	HashFunction hf;
	
	public HashingPatternCodec(HashFunction hf) {
		this.hf = hf;
	}

	private String iterativeEncode(QueryGraph graph, QueryGraphNode root,
			HashSet<QueryGraphNode> visited) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;

		visited.add(root);
		
		sb.append(root.getHashLabel(hf));			

		List<Connectivity> linked = root.getConnectivities();
		java.util.Collections.sort(linked, new ConnectivityComparator());

		for (Connectivity con : linked)
			if (!visited.contains(con.getNode())) {
				if (first) {
					sb.append("[");
					first = false;
				} else
					sb.append(",");
				sb.append((con.isOutEdge() ? '+' : '-')
						+ con.getEdge().getLabel());
				sb.append("::");
				sb.append(iterativeEncode(graph, con.getNode(), visited));
			}

		if (!first)
			sb.append("]");
		return sb.toString();
	}

	/**
	 * Encode a tree-shaped pattern by calculating its preorder string
	 */
	@Override
	public String encodePattern(QueryGraph graph) {
		int cmpRes;
		
		if (graph.nodeCount() == 0)
			return "";
		
		QueryGraphNode minNode = graph.getNode(0);
		
		if (graph.nodeCount() == 1)
			return minNode.getLabel();

		for (int i = 1; i < graph.nodeCount(); i++) {
			QueryGraphNode currentNode = graph.getNode(i);
			if ((cmpRes = currentNode.getHashLabel(hf).compareTo(minNode.getHashLabel(hf))) < 0)
				minNode = currentNode;
			else if (cmpRes == 0 && (cmpRes = (currentNode.getOutDegree() - minNode.getOutDegree())) > 0)
				minNode = currentNode;
			else if (cmpRes == 0 && (cmpRes = (currentNode.getInDegree() - minNode.getInDegree())) > 0)
				minNode = currentNode;
		}

		return iterativeEncode(graph, minNode, new HashSet<QueryGraphNode>());
	}

	private void iterativeDecode(QueryGraph g, QueryGraphNode last,
			String pattern, boolean isHash) {
		String header, content, nodeLabel, edgeLabel;
		boolean outEdge;
		int pos;

		pos = pattern.indexOf("[");
		if (pos < 0) {
			header = pattern;
			content = null;
		} else {
			header = pattern.substring(0, pos);
			content = pattern.substring(pos + 1, pattern.lastIndexOf("]"));
		}

		pos = header.indexOf("::");
		if (pos < 0) {
			nodeLabel = header;
			outEdge = false;
			edgeLabel = null;
		} else {
			nodeLabel = header.substring(pos + 2, header.length());
			outEdge = (header.charAt(0) == '+');
			edgeLabel = header.substring(1, pos);
		}

		QueryGraphNode n;
		if (isHash && !nodeLabel.equals(wildcard))
			n = g.addNode(nodeLabel, true); 
		else
			n = g.addNode();
		
		if (edgeLabel != null) {
			if (outEdge)
				g.addEdge(last, n, edgeLabel);
			else
				g.addEdge(n, last, edgeLabel);
		}

		if (content != null) {
			List<Integer> splitPos = new ArrayList<Integer>();
			int dep = 0;
			splitPos.add(-1);
			for (int i = 0; i < content.length(); i++)
				if (content.charAt(i) == '[')
					dep++;
				else if (content.charAt(i) == ']')
					dep--;
				else if (content.charAt(i) == ',' && dep == 0)
					splitPos.add(i);
			splitPos.add(content.length());

			for (int i = 1; i < splitPos.size(); i++)
				iterativeDecode(g, n, content.substring(
						splitPos.get(i - 1) + 1, splitPos.get(i)), isHash);
		}
	}

	/**
	 * 
	 */
	public QueryGraph decodePattern(String pattern, boolean isHash) {
		QueryGraph g = new QueryGraph();

		iterativeDecode(g, null, pattern, isHash);
		return g;
	}

	/**
	 * The comparator that compares two connectivity according to the
	 * lexicographic
	 * 
	 * @author Tian Yuan
	 * 
	 */
	class ConnectivityComparator implements Comparator<Connectivity> {

		@Override
		public int compare(Connectivity o1, Connectivity o2) {
			int cmpRes;
			
			Connectivity a = (Connectivity) o1;
			Connectivity b = (Connectivity) o2;
			
			if ((cmpRes = a.getNode().getHashLabel(hf).compareTo(b.getNode().getHashLabel(hf))) != 0)
				return cmpRes;
			else if ((cmpRes = a.getEdge().getLabel().compareTo(b.getEdge().getLabel())) != 0)
				return cmpRes;
			else
				return 0;	
		}

	}

	@Override
	public QueryGraph decodePattern(String pattern) {
		return decodePattern(pattern, false);
	}

}
