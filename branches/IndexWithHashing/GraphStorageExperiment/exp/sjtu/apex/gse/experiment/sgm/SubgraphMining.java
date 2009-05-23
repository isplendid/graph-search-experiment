package sjtu.apex.gse.experiment.sgm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.FileQueryWriter;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.QueryWriter;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.util.Heap;

public class SubgraphMining {
	
	static final double sampleRate = 0.2;
	
	public static void mine(int mod, String srcPath, String dest, int freq) {
		ModHash hf = new ModHash(mod);
		HashingPatternCodec codec = new HashingPatternCodec(hf);
		
		File f = new File(srcPath);
		File[] qFile = f.listFiles();
		List<QueryGraph> list = new ArrayList<QueryGraph>();
		SubgraphControl sc = new SubgraphControl();
		Heap h = new Heap();
		

		for (File q : qFile) {
			QueryReader rd = new FileQueryReader(q.getAbsolutePath());
			QuerySchema sch;
			
			while ((sch = rd.read()) != null) 
				if (Math.random() < sampleRate)
					list.add(sch.getQueryGraph());
		
			rd.close();
		}
		
		for (QueryGraph g : list) 
			for (int i = g.edgeCount() - 1; i >= 0; i--) {
				QueryGraphEdge edge = g.getEdge(i);
				SubgraphInfo si;
				
				String shash = edge.getNodeFrom().getHashLabel(hf);
				String ohash = edge.getNodeTo().getHashLabel(hf);
				
				QueryGraph ng;
				Map<QueryGraphNode, QueryGraphNode> tm;
				
				if (!shash.equals("*") && !ohash.equals("*")) {
					ng = new QueryGraph();
					ng.addEdge(ng.addNode(shash, true), ng.addNode(ohash, true), edge.getLabel());
					si = sc.getInfo(ng, codec);
					tm = new HashMap<QueryGraphNode, QueryGraphNode>();
					tm.put(si.getPattern().getEdge(0).getNodeFrom(), edge.getNodeFrom());
					tm.put(si.getPattern().getEdge(0).getNodeTo(), edge.getNodeTo());
					si.addMap(tm);
				}
				
				if (!ohash.equals("*")) {
					ng = new QueryGraph();
					ng.addEdge(ng.addNode(), ng.addNode(ohash, true), edge.getLabel());
					si = sc.getInfo(ng, codec);
					tm = new HashMap<QueryGraphNode, QueryGraphNode>();
					tm.put(si.getPattern().getEdge(0).getNodeFrom(), edge.getNodeFrom());
					tm.put(si.getPattern().getEdge(0).getNodeTo(), edge.getNodeTo());
					si.addMap(tm);
				}
				
				if (!shash.equals("*")) {
					ng = new QueryGraph();
					ng.addEdge(ng.addNode(shash, true), ng.addNode(), edge.getLabel());
					si = sc.getInfo(ng, codec);
					tm = new HashMap<QueryGraphNode, QueryGraphNode>();
					tm.put(si.getPattern().getEdge(0).getNodeFrom(), edge.getNodeFrom());
					tm.put(si.getPattern().getEdge(0).getNodeTo(), edge.getNodeTo());
					si.addMap(tm);
				}
			}
		
		for (SubgraphInfo si : sc.getAllInfo())
			if (si.getInstanceCount() > freq)
				h.insert(si);
		
		QueryWriter wr = new FileQueryWriter(dest);
		
		while (h.size() != 0) {
			SubgraphInfo si = (SubgraphInfo)h.remove();
			
			
		}
	}
	
	public void main(String[] args) {
		
	}
	
}
