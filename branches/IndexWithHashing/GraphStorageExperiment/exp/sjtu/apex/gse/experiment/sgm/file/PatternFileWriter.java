package sjtu.apex.gse.experiment.sgm.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;

public class PatternFileWriter {
	BufferedWriter wr;
	
	public PatternFileWriter(String filename) {
		try {
			wr = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void write(QueryGraph g, HashFunction hf) {
		Map<QueryGraphNode, Integer> map = new HashMap<QueryGraphNode, Integer>();
		
		try {
			wr.append(g.nodeCount() + " " + g.edgeCount() + "\n");
			int count;
			count = g.nodeCount();
			
			for (int i = 0; i < count; i++) {
				if (g.getNode(i).isGeneral())
					wr.append("*\n");
				else
					wr.append(g.getNode(i).getHashLabel(hf) + "\n");
				
				map.put(g.getNode(i), i + 1);
			}
			count = g.edgeCount();
			for (int i = 0; i < count; i++)
				wr.append(map.get(g.getEdge(i).getNodeFrom()) + " " + map.get(g.getEdge(i).getNodeTo()) + " " + g.getEdge(i).getLabel() + "\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
