package sjtu.apex.gse.indexer.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.pattern.HashingPatternCodec;
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
	static final boolean extendEdge = false;
	
	private int maxSize;
	private FileIndexer fidx;
	private IDManager idman;
	private LabelManager lblman;
	private HashingPatternCodec codec;
	private HashFunction hash;
	private Configuration config;
	private ColumnNodeMap cnm;
	
	public IterativeIndexer(Configuration config) {
		this.config = config;
		maxSize = config.getIntegerSetting("PatternLength", 3);
		fidx = new FileIndexer(maxSize, config);
		hash = new ModHash(config);
		codec = new HashingPatternCodec(hash);
		idman = new SleepyCatIDManager(config);
		lblman = new SleepyCatLabelManager(config);
		maxSize = config.getIntegerSetting("PatternLength", 3);
		cnm = new HashLexicoColumnNodeMap(hash);
	}
	
	public void indexComplexPatterns(int minJoinInsCnt, int totalThreshold, String edges) {
		List<String> elabels = new ArrayList<String>();
		Set<String> extended = new HashSet<String>();
		QuerySystem sys = new QuerySystem(config);
		
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
		
		String sf = config.getStringSetting("DataFolder", null);
		int spsl = config.getIntegerSetting("PatternStrSize", 128);
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
        			
        			h.insert(new HeapContainer(ps, codec.decodePattern(ps, true), insCnt));
        		}
        	}
        	fir.close();
        }


        while ((hc = (HeapContainer)h.remove()) != null && tc < totalThreshold) {
        	System.out.println(hc.patternStr);
        	
        	QueryGraph g = hc.graph;
        	int psize = g.nodeCount();
        	
        	if (sys.patternManager().getPatternInstanceCount(hc.patternStr, psize) == null) {
        		sys.indexManager().close();
        		fidx.flush(psize);
        	}
        	
        	boolean tag = false;

        	//Check if node can be extended
        	
        	for (int i = psize - 1; i >= 0; i--)
        		if (g.getNode(i).isGeneral()) {
        			System.out.println("Extending node constraints");
        			QueryGraphNode extNode = g.getNode(i);
        			
        			
        			Scan s = sys.queryPlanner().plan(getFullSchema(g)).open();
        			
        			int tmpcntr = 0;
        			
        			Map<Integer, HashCatContainer> nps = new HashMap<Integer, HashCatContainer>();
					
        			while (s.next()) {
        				
        				if ((++tmpcntr) % 1000 == 0)
        					System.out.println("\t" + tmpcntr + " entries checked, total " + tc + " entries.");
        				
        				int ni = s.getID(extNode);
        				String[] sl = lblman.getLabel(idman.getURI(ni));
        				
        				if (sl != null) {
        					Set<Integer> visited = new HashSet<Integer>();
        					for (int j = sl.length - 1; j >= 0; j--) {
        						int lb = hash.hashInt(sl[j]);
        						        						
        						if (visited.contains(lb))
        							continue;
        					
        						visited.add(lb);
        						
        						HashCatContainer hcc;
        						
        						if ((hcc = nps.get(lb)) == null) {
        							QueryGraph ng = GraphUtility.extendConstraint(g, i, Integer.toString(lb), true);
        							hcc = new HashCatContainer(ng, extended);
        							
        							nps.put(lb, hcc);
        						}
        						
        						if (!hcc.checked) {
        							int[] ins = new int[psize];
        							for (int k = 0; k < psize; k++)
        								ins[hcc.cm.get(g.getNode(k))] = s.getID(g.getNode(k));

        							tc ++;
        							hcc.cntr ++;

        							tag = true;
        							fidx.addEntry(hcc.ps, psize, ins);
        						}
        					}
        				}
        			}
        			
        			s.close();
        			
        			for (Entry<Integer, HashCatContainer> e : nps.entrySet()) {
        				HashCatContainer hcc = e.getValue();
        				
        				extended.add(hcc.ps);
        				if (hcc.cntr > minJoinInsCnt)
        					h.insert(new HeapContainer(hcc.ps, hcc.graph, hcc.cntr));
        			}
        			
        			
        		}
        	
        	//If no node gets extended on constraints, we extend edges
        	if (extendEdge && !tag && g.nodeCount() < maxSize) {
        		System.out.println("Extending edges...");
        		int labelCount = elabels.size(); 
        		
        		for (int toExt = psize - 1; toExt >= 0; toExt--) {
        			
        			for (int j = labelCount - 1; j >= 0; j --) {
        				String el = elabels.get(j);        				
        				
        				System.out.println("    Querying +Edge '" + el + "' ...");
        				
        				for (boolean dir = true; dir != false; dir = !dir) {
        					QueryGraph ng = GraphUtility.extendEdge(g, toExt, el, dir, true);        					
        					
        					int cnt = batchAddPattern(extended, ng, sys, (int)(hc.insCnt * coef));
        					
        					tc += cnt;
        					
        					if (cnt > minJoinInsCnt)
            					h.insert(new HeapContainer(codec.encodePattern(ng), ng, cnt));
        				}
        				
        			}
        		}
        	}
        	
        }
        
        sys.indexManager().close();
	}
	
	public void close() {
		lblman.close();
		idman.close();
		fidx.close();
	}
	
	private void loadKeyword(String filename) {
        InstanceKeywordRepository rd = new InstanceKeywordRepository(filename);
        lblman.load(rd);
        rd.close();
	}
	
	private int batchAddPattern(Set<String> extended, QueryGraph graph, QuerySystem sys, int maxInsCnt) {
		String ps = codec.encodePattern(graph);
		
		if (extended.contains(ps))
			return 0;
		extended.add(ps);

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
		
		ts.close();
		
		
		if (cnt >= maxInsCnt) return 0;
		
		for (int[] ins : list)
			fidx.addEntry(ps, graph.nodeCount(), ins);
		
		System.out.println("Pattern " + ps + " added with " + cnt + " instances");
		
		return cnt;
	}
	
	private QuerySchema getFullSchema(QueryGraph g) {
		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
		for (int j = g.nodeCount() - 1; j >= 0; j--)
			nset.add(g.getNode(j));
		return new QuerySchema(g, nset);
	}

	static public void main(String[] args) {
		IterativeIndexer idx = new IterativeIndexer(new FileConfig(args[0]));
		
		
		idx.loadKeyword(args[2]);
		idx.indexComplexPatterns(5000, 50000000, args[1]);	
		idx.close();
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
	
	class HashCatContainer {
		int cntr = 0;
		QueryGraph graph;
		Map<QueryGraphNode, Integer> cm;
		String ps;
		boolean checked;
		
		public HashCatContainer(QueryGraph graph, Set<String> extended) {
			this.graph = graph;
			ps = codec.encodePattern(graph);
			cm = cnm.getMap(graph);
			this.checked = extended.contains(ps); 
		}
	}
}
