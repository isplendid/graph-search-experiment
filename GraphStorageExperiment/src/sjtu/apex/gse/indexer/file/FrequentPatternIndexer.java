package sjtu.apex.gse.indexer.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.experiment.sgm.file.PatternFileReader;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.util.SimplePatternControl;
import sjtu.apex.gse.indexer.util.SimplePatternControl.Pair;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.storage.file.FileRepository;
import sjtu.apex.gse.storage.file.SourceHeapReader;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class FrequentPatternIndexer {
	
	final static double coef = 0.5;
	
	private Configuration config;
	private FileIndexer fidx;
	private int maxsize;
	private HashingPatternCodec codec;
	private HashFunction hash;
	private IDManager idman;
	private LabelManager lblman;
	private ColumnNodeMap cnm;
	
	public FrequentPatternIndexer(Configuration config) {
		maxsize = config.getIntegerSetting("PatternLength", 3);
		fidx = new FileIndexer(maxsize, config);
		hash = new ModHash(config);
		codec = new HashingPatternCodec(hash);
		idman = new SleepyCatIDManager(config);
		lblman = new SleepyCatLabelManager(config);
		cnm = new HashLexicoColumnNodeMap(hash);
		this.config = config;
	}
	
	public void indexFrequentPattern(String ptFile) {
		QuerySystem sys = new QuerySystem(config);
		PatternFileReader pr = new PatternFileReader(ptFile);
		List<QueryGraph>[] ptlist = new List[maxsize - 1]; 
		QueryGraph g;
		
		for (int i = 0; i < maxsize - 1; i++) ptlist[i] = new ArrayList<QueryGraph>();
		
		while ((g = pr.read()) != null) {
			int gsize = g.nodeCount();
			
			if (gsize <= maxsize)
				ptlist[gsize - 2].add(g);
		}
		
		pr.close();
		
		indexSingle(ptlist, sys);
		sys.indexManager().close();
		fidx.flush(2);
		for (int i = 3; i <= maxsize; i++) {
			indexComplex(ptlist[i - 2], sys);
			sys.indexManager().close();
			fidx.flush(i);
		}
	}
	
	private void indexSingle(List<QueryGraph>[] list, QuerySystem sys) {
		SimplePatternControl spc = new SimplePatternControl((ModHash)hash);
		String storeFn = config.getStringSetting("DataFolder", null) + "/storage1";
		int cnt = 0;
		Map<QueryGraph, Map<QueryGraphNode, Integer>> map = new HashMap<QueryGraph, Map<QueryGraphNode, Integer>>();
		Map<QueryGraph, String> ps = new HashMap<QueryGraph, String>();
		
		for (QueryGraph g : list[0]) {
			spc.addPattern(g);
			map.put(g, cnm.getMap(g));
			ps.put(g, codec.encodePattern(g));
		}
		
		for (int i = 1; i < list.length; i++)
			for (QueryGraph g : list[i]) 
				for (int j = 0; j < g.edgeCount(); j++) {
					QueryGraphEdge edge = g.getEdge(j);
					
					if (edge.getNodeFrom().isGeneral() && edge.getNodeTo().isGeneral()) continue;
					
					String elabel = edge.getLabel();
					String shash = edge.getNodeFrom().getHashLabel(hash);
					String ohash = edge.getNodeTo().getHashLabel(hash);
					if (!spc.containsPattern(elabel, shash, ohash)) {
						System.out.println(shash + "-[" + elabel + "]->" + ohash);
						QueryGraph ng = new QueryGraph();
						ng.addEdge(ng.addNode(shash, true), ng.addNode(ohash, true), elabel);
						spc.addPattern(ng);
						map.put(ng, cnm.getMap(ng));
						ps.put(ng, codec.encodePattern(ng));
					}
				}
		
		for (String el : spc.getLabelSet()) 
			for (Pair p : spc.getPairs(el)) {
			String shash = p.getSub();
			String ohash = p.getObj();
			
			if (!shash.equals("*") && !ohash.equals("*")) {
				if (!spc.containsPattern(el, "*", ohash)) {
					System.out.println("*-[" + el + "]->" + ohash);
					QueryGraph ng = new QueryGraph();
					ng.addEdge(ng.addNode(), ng.addNode(ohash, true), el);
					spc.addPattern(ng);
					map.put(ng, cnm.getMap(ng));
					ps.put(ng, codec.encodePattern(ng));
				}
				if (!spc.containsPattern(el, shash, "*")) {
					System.out.println(shash + "-[" + el + "]->*");
					QueryGraph ng = new QueryGraph();
					ng.addEdge(ng.addNode(shash, true), ng.addNode(), el);
					spc.addPattern(ng);
					map.put(ng, cnm.getMap(ng));
					ps.put(ng, codec.encodePattern(ng));
				}
			}
			
		}
		
		SourceHeapReader heap = new SourceHeapReader(config.getStringSetting("DataFolder", null) + "/sources");
//		return;
		for (String el : spc.getLabelSet()) {
			System.out.println((++cnt) + " : " + el);
			Scan s = new FileRepository(sys.indexManager(), storeFn, "*[+" + el + "::*]", 2, heap);
		
			while (s.next()) {
				
				int sub = s.getID(0);
				int obj = s.getID(1);
				
//				System.out.println(idman.getURI(sub) + " " + idman.getURI(obj));
				
				String[] slab = lblman.getLabel(idman.getURI(sub));
				String[] olab = lblman.getLabel(idman.getURI(obj));
				
				List<QueryGraph> eg = spc.getAvailPatterns(el, slab, olab);
				
				for (QueryGraph tg : eg) {
					
					int[] ins = new int[2];
					Map<QueryGraphNode, Integer> tcnm = map.get(tg);
					
					ins[tcnm.get(tg.getEdge(0).getNodeFrom())] = sub;
					ins[tcnm.get(tg.getEdge(0).getNodeTo())] = obj;
					fidx.addEntry(ps.get(tg), 2, ins);
				}
			}
			
			s.close();
		}
	}
	
	private void indexComplex(List<QueryGraph> list, QuerySystem sys) {
		for (QueryGraph g : list)
			batchAddPattern(g, sys);
	}
	
	private int batchAddPattern(QueryGraph graph, QuerySystem sys) {
		String ps = codec.encodePattern(graph);
		
		Plan p = sys.queryPlanner().plan(new QuerySchema(graph, graph.getNodeSet()));
		
		int maxInsCnt = (int)(p.diskIO() * coef);
		
		Scan ts = p.open();
		ArrayList<int[]> list = new ArrayList<int[]>();
		int nodeCnt = graph.nodeCount();
		
		Map<QueryGraphNode, Integer> map = cnm.getMap(graph);
		
		int[] tmp = new int[nodeCnt];
		for (int k = 0; k < nodeCnt; k++)
			tmp[k] = map.get(graph.getNode(k));
		
		int cnt = 0;
		
		while (ts.next() && cnt < maxInsCnt) {
			int[] ins = new int[nodeCnt];
			
			for (int k = nodeCnt - 1; k >= 0; k--)
				ins[tmp[k]] = ts.getID(graph.getNode(k));
			
			list.add(ins);
			cnt++;
		}
		
		ts.close();
		
		
		if (cnt >= maxInsCnt) return 0;
		
		for (int[] ins : list)
			fidx.addEntry(ps, graph.nodeCount(), ins);
		
		System.out.println("Pattern " + ps + " added with " + cnt + " instances");
		
		return cnt;
	}
	
	public void close() {
		lblman.close();
		idman.close();
		fidx.close();
	}
	
	public static void main(String[] args) {
		FrequentPatternIndexer fpi = new FrequentPatternIndexer(new FileConfig(args[0]));
		
		fpi.indexFrequentPattern(args[1]);
		
		fpi.close();
	}
}