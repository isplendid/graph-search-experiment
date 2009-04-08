package sjtu.apex.gse.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class FileQueryParser implements QueryParser {
	BufferedReader rd = null;

	public FileQueryParser(String filename) {
		try {
			rd =  new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public QuerySchema getNext() {
		try {
			String temp;
			String[] ts;
			QueryGraph graph = new QueryGraph();

			temp = rd.readLine();
			if (temp == null || temp.length() == 0) return null;
			
			ts = temp.split(" ");
			int nodeCnt = Integer.parseInt(ts[0]);
			int edgeCnt = Integer.parseInt(ts[1]);

			QueryGraphNode[] nodes = new QueryGraphNode[nodeCnt];
			HashSet<QueryGraphNode> ns = new HashSet<QueryGraphNode>();
			for (int i = 0; i < nodeCnt; i++) {
				temp = rd.readLine();
				if (temp.length() == 0 || temp.equals("*"))
					nodes[i] = graph.addNode();
				else
					nodes[i] = graph.addNode(temp);

				ns.add(nodes[i]);
			}

			for (int i = 0; i < edgeCnt; i++) {
				temp = rd.readLine();
				ts = temp.split(" ");
				int a = Integer.parseInt(ts[0]), b = Integer.parseInt(ts[1]);

				graph.addEdge(nodes[a - 1], nodes[b - 1], ts[2]);
			}

			return new QuerySchema(graph, ns);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void close() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
