package sjtu.apex.gse.experiment.sgm.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;

public class PatternFileReader {
	BufferedReader rd = null;
	IDManager idman;

	public PatternFileReader(String filename, IDManager idman) {
		try {
			rd =  new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public QueryGraph read() {
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
			for (int i = 0; i < nodeCnt; i++) {
				temp = rd.readLine();
				if (temp.length() == 0 || temp.equals("*"))
					nodes[i] = graph.addNode();
				else
					nodes[i] = graph.addNode(idman.addGetID(temp), true);

			}

			for (int i = 0; i < edgeCnt; i++) {
				temp = rd.readLine();
				ts = temp.split(" ");
				int a = Integer.parseInt(ts[0]), b = Integer.parseInt(ts[1]);

				graph.addEdge(nodes[a - 1], nodes[b - 1], idman.addGetID(ts[2]));
			}

			return graph;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void close() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
