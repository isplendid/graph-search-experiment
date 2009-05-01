package sjtu.apex.gse.indexer.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.struct.GraphUtility;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;
import sjtu.apex.gse.util.Heap;

public class IterativeIndexer {
	
	static final double coef = 0.9;
	
	private int maxSize;
	private FileIndexer fidx;
	private IDManager idman;
	private LabelManager lblman;
	private PatternCodec codec;
	private HashFunction hash;
	private ColumnNodeMap cnm;
	
	public IterativeIndexer(Configuration config) {
		maxSize = config.getIntegerSetting("PatternLength", 3);
		fidx = new FileIndexer(maxSize, config);
		hash = new ModHash(config);
		codec = new HashingPatternCodec(hash);
		idman = new SleepyCatIDManager(config);
		lblman = new SleepyCatLabelManager(config);
		maxSize = config.getIntegerSetting("PatternLength", 3);
		cnm = new HashLexicoColumnNodeMap(hash);
	}
	
	public void indexComplexPatterns(int minJoinInsCnt, int totalThreshold, Configuration source, String edges) {
		List<String> elabels = new ArrayList<String>();
		
		BufferedReader rd;
		try {
			rd = new BufferedReader(new FileReader(edges));
			String temp;
			while ((temp = rd.readLine()) != null)
				elabels.add(temp);
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		QuerySystem sys = new QuerySystem(source);
		String sf = source.getStringSetting("DataFolder", null);
		int spsl = source.getIntegerSetting("PatternStrSize", 128);
		int hashMod = source.getIntegerSetting("HashMod", 1);
        Heap h = new Heap();
        HeapContainer hc;
        int tc = 0;
        
        for (int i = 1; i < maxSize; i++) {
        	String tidx = sf + "/index" + i;
        	FileIndexReader fir = new FileIndexReader(tidx, i + 1, spsl);
        
        	while (fir.next()) {
        		int insCnt = fir.getInstanceCount();
        		
        		if (insCnt > minJoinInsCnt) {
        			String ps = fir.getPatternString();
        			
        			h.insert(new HeapContainer(ps, codec.decodePattern(ps), insCnt));
        		}
        	}
        	fir.close();
        }


        while ((hc = (HeapContainer)h.remove()) != null && tc < totalThreshold) {
        	QueryGraph g = hc.graph;
        	boolean tag = false;

        	//Check if node can be extended
        	for (int i = g.nodeCount() - 1; i >= 0; i--)
        		if (g.getNode(i).isGeneral()) {
        			QueryGraphNode extNode = g.getNode(i);
        			
        			Scan s = sys.queryPlanner().plan(getFullSchema(g)).open();
        			Set<String> avl = new HashSet<String>();
        			
        			while (s.next() && avl.size() < hashMod) {
        				int ni = s.getID(extNode);
        				String[] sl = lblman.getLabel(idman.getURI(ni));
        				
        				for (int j = sl.length - 1; j >= 0; j--)
        					avl.add(hash.hashStr(sl[j]));
        			}
        			
        			for (String lb : avl) {
        				QueryGraph ng = GraphUtility.extendConstraint(g, i, lb);
        				
        				int cnt = batchAddPattern(ng, sys, hc.insCnt);
        				
        				tc += cnt;
        				
        				if (cnt > minJoinInsCnt)
        					h.insert(new HeapContainer(codec.encodePattern(ng), ng, cnt));
        				
        			}
        		}
        	
        	//If no node gets extended on constraints, we extend edges
        	if (!tag) {
        		int nodeCount = g.nodeCount(), labelCount = elabels.size(); 
        		
        		for (int toExt = nodeCount - 1; toExt >= 0; toExt--) {
        			
        			System.out.println("  Extend edges from node " + toExt);
        			
        			for (int j = labelCount - 1; j >= 0; j --) {
        				String el = elabels.get(j);        				
        				
        				System.out.println("    Querying [+Edge :: " + el + "] ...");
        				
        				for (boolean dir = true; dir != false; dir = !dir) {
        					QueryGraph ng = GraphUtility.extendEdge(g, toExt, el, dir);        					
        					
        					int cnt = batchAddPattern(ng, sys, (int)(hc.insCnt * coef));
        					
        					tc += cnt;
        					
        					if (cnt > minJoinInsCnt)
            					h.insert(new HeapContainer(codec.encodePattern(ng), ng, cnt));
        				}
        				
        			}
        		}
        	}
        	
        }
	}
	
	private int batchAddPattern(QueryGraph graph, QuerySystem sys, int maxInsCnt) {
		Scan ts = sys.queryPlanner().plan(getFullSchema(graph)).open();
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
		
		if (cnt < maxInsCnt)
			for (int[] ins : list)
				fidx.addEntry(codec.encodePattern(graph), graph.nodeCount(), ins);
		
		
		return cnt;
	}
	
	private QuerySchema getFullSchema(QueryGraph g) {
		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
		for (int j = g.nodeCount() - 1; j >= 0; j--)
			nset.add(g.getNode(j));
		return new QuerySchema(g, nset);
	}

	public void main(String[] args) {
		IterativeIndexer idx = new IterativeIndexer(new FileConfig(args[0]));
		
		idx.indexComplexPatterns(5000, 50000000, new FileConfig(args[1]), args[2]);		
	}
	
	class HeapContainer implements Comparable<Object> {
        String patternStr;
        QueryGraph graph;
        int insCnt;

        HeapContainer(String patternStr, QueryGraph g, int c) {
                this.patternStr = patternStr;
                this.graph = g;
                this.insCnt = c;
        }

        @Override
        public int compareTo(Object arg0) {
                if (arg0 instanceof HeapContainer)
                        return insCnt - ((HeapContainer) arg0).insCnt;
                else
                        return 0;
        }

	}
}
