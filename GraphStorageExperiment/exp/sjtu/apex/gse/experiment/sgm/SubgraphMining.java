package sjtu.apex.gse.experiment.sgm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.experiment.sgm.file.PatternFileWriter;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.GraphUtility;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.util.Heap;

public class SubgraphMining {
	
	static final double sampleRate = 0.2;
	
	private static Map<QueryGraphNode, QueryGraphNode> getEdgeExtendedMap(QueryGraph ng, ExtendGroup eg) {
		Map<QueryGraphNode, QueryGraphNode> nmap = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (int j = ng.nodeCount() - 1; j >= 0; j--) {
			QueryGraphNode on = ng.getNode(j);
			QueryGraphNode tn = eg.getMap().get(on);
			
			if (tn == null) tn = eg.nn;
			
			nmap.put(on, tn);
		}
		
		return nmap;
	}
	
	private static Map<QueryGraphNode, QueryGraphNode> getNodeExtendedMap(QueryGraph ng, ExtendGroup eg) {
		Map<QueryGraphNode, QueryGraphNode> nmap = new HashMap<QueryGraphNode, QueryGraphNode>();
		
		for (int j = ng.nodeCount() - 1; j >= 0; j--) {
			QueryGraphNode on = ng.getNode(j);
			nmap.put(on, eg.getMap().get(on));
		}
		
		return nmap;
	}
	
	public static void mine(int mod, String srcPath, String dest, int freq, int maxsize) {
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
		
		Set<String> extended = new HashSet<String>();
		
		for (String s : sc.getAllPatterns()) {
			SubgraphInfo si = sc.getSubgraphInfo(s);
			
			if (si.getInstanceCount() > freq) {
				h.insert(si);
				extended.add(s);
			}
		}
		
		PatternFileWriter wr = new PatternFileWriter(dest);
		
		int cnt = 0;
		
		while (h.size() != 0) {
			System.out.println(++cnt);
			
			SubgraphInfo si = (SubgraphInfo)h.remove();
			QueryGraph g = si.getPattern();
			wr.write(g, hf);
			
			for (int i = g.nodeCount() - 1; i >= 0; i--) {
				QueryGraphNode node = g.getNode(i);
				if (g.nodeCount() < maxsize) {
					ExtendList eel = new ExtendList();

					for (Map<QueryGraphNode, QueryGraphNode> m : si.getMap()) {
						Collection<QueryGraphNode> mappedNode = m.values();
						
						for (Connectivity con : m.get(node).getConnectivities())
							if (!mappedNode.contains(con.getNode())) {
								String tl = (con.isOutEdge() ? "+" + con.getEdge().getLabel() : "-" + con.getEdge().getLabel()); 
								eel.add(tl, m, con.getNode());
							}
					}
					
					for (String s : eel.getAllLabel()) {
						String label = s.substring(1);
						boolean dir = s.startsWith("+");
						QueryGraph ng = GraphUtility.extendEdge(g, i, label, dir, true);
						
						String ps = codec.encodePattern(ng);
						
						if (extended.contains(ps)) continue;
						
						
						SubgraphInfo nsi = new SubgraphInfo(ng);
						for (ExtendGroup eg : eel.getGroups(s))
							nsi.addMap(getEdgeExtendedMap(ng, eg));
						
						if (nsi.getInstanceCount() > freq)
							h.insert(nsi);
						extended.add(ps);
					}
				}
				
				if (node.isGeneral()) {
					ExtendList eel = new ExtendList();
					
					for (Map<QueryGraphNode, QueryGraphNode> m : si.getMap()) {
						QueryGraphNode mappedNode = m.get(node);
						if (!mappedNode.isGeneral())
							eel.add(mappedNode.getHashLabel(hf), m, mappedNode);
					}
					
					for (String s : eel.getAllLabel()) {
						QueryGraph ng = GraphUtility.extendConstraint(g, i, s, true);
						
						String ps = codec.encodePattern(ng);
						
						if (extended.contains(ps)) continue;
						
						SubgraphInfo nsi = new SubgraphInfo(ng);
						
						for (ExtendGroup eg : eel.getGroups(s))
							nsi.addMap(getNodeExtendedMap(ng, eg));
						
						if (nsi.getInstanceCount() > freq)
							h.insert(nsi);
						extended.add(ps);
					}
					
				}
			}
			
		}
		wr.close();
	}
	
	static public void main(String[] args) {
		SubgraphMining.mine(Integer.parseInt(args[0]), args[1], args[2], Integer.parseInt(args[3]), 3);
	}
	
}
