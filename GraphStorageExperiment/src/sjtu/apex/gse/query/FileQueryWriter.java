package sjtu.apex.gse.query;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class FileQueryWriter implements QueryWriter {
	BufferedWriter wr;
	
	public FileQueryWriter(String filename) {
		try {
			wr = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(QuerySchema sch) {
		QueryGraph g = sch.getQueryGraph();
		Map<QueryGraphNode, Integer> map = new HashMap<QueryGraphNode, Integer>();
		
		try {
			wr.append(g.nodeCount() + " " + g.edgeCount() + "\n");
			int count;
			count = g.nodeCount();
			
			for (int i = 0; i < count; i++) {
				if (g.getNode(i).isGeneral())
					wr.append("\n");
				else
					wr.append(g.getNode(i).getLabel());
				
				map.put(g.getNode(i), i + 1);
			}
			count = g.edgeCount();
			for (int i = 0; i < count; i++)
				wr.append(map.get(g.getEdge(i).getNodeFrom()) + " " + g.getEdge(i).getNodeTo() + " " + g.getEdge(i).getLabel() + "\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
