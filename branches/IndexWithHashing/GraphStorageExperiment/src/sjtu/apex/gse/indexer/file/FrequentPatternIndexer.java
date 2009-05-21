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
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.storage.file.FileRepository;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.struct.QueryGraph;
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
		
		indexSingle(ptlist[0], sys);
		fidx.flush(2);
//		for (int i = 3; i <= maxsize; i++) {
//			indexComplex(ptlist[i - 2], sys);
//			fidx.flush(i);
//		}
	}
	
	private void indexSingle(List<QueryGraph> list, QuerySystem sys) {
		SimplePatternControl spc = new SimplePatternControl((ModHash)hash);
		String storeFn = config.getStringSetting("DataFolder", null) + "/storage1";
		int cnt = 0;
		Map<QueryGraph, Map<QueryGraphNode, Integer>> map = new HashMap<QueryGraph, Map<QueryGraphNode, Integer>>();
		Map<QueryGraph, String> ps = new HashMap<QueryGraph, String>();
		
		for (QueryGraph g : list) {
			spc.addPattern(g);
			map.put(g, cnm.getMap(g));
			ps.put(g, codec.encodePattern(g));
		}
		
		if ((++cnt) % 100 == 0) System.out.println(cnt);
		
		for (String el : spc.getLabelSet()) {
			Scan s = new FileRepository(sys.indexManager(), storeFn, "*[+" + el + "::*]", 2);
		
			while (s.next()) {
				
				int sub = s.getID(0);
				int obj = s.getID(1);
				
//				System.out.println(idman.getURI(sub) + " " + idman.getURI(obj));
				
				String[] slab = lblman.getLabel(idman.getURI(sub));
				String[] olab = lblman.getLabel(idman.getURI(obj));
				
				List<QueryGraph> eg = spc.getAvailPatterns(el, slab, olab);
				
				for (QueryGraph tg : eg) {
					
					int[] ins = new int[2];
					ins[map.get(tg).get(tg.getEdge(0).getNodeFrom())] = sub;
					ins[map.get(tg).get(tg.getEdge(0).getNodeTo())] = obj;
					fidx.addEntry(ps.get(tg), 2, ins);
				}
			}
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
