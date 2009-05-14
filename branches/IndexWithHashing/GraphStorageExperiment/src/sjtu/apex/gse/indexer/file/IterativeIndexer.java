package sjtu.apex.gse.indexer.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.experiment.edge.Edge;
import sjtu.apex.gse.experiment.edge.EdgeInfo;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.struct.GraphUtility;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class IterativeIndexer {
	
	static final double coef = 0.9;
	
	private boolean extendEdge;
	private int maxSize;
	private FileIndexer fidx;
	private IDManager idman;
	private LabelManager lblman;
	private HashingPatternCodec codec;
	private HashFunction hash;
	private Configuration config;
	private ColumnNodeMap cnm;
	private int minJoinInsCnt, totalThreshold;
	
	public IterativeIndexer(Configuration config, int minJoinInsCnt, int totalThreshold) {
		this.config = config;
		maxSize = config.getIntegerSetting("PatternLength", 3);
		extendEdge = (config.getIntegerSetting("IndexComplex", 0) != 0);
		fidx = new FileIndexer(maxSize, config);
		hash = new ModHash(config);
		codec = new HashingPatternCodec(hash);
		idman = new SleepyCatIDManager(config);
		lblman = new SleepyCatLabelManager(config);
		maxSize = config.getIntegerSetting("PatternLength", 3);
		cnm = new HashLexicoColumnNodeMap(hash);
		this.minJoinInsCnt = minJoinInsCnt;
		this.totalThreshold = totalThreshold;
	}
	
	private void loadInitials(PatternPool pp) {
		String sf = config.getStringSetting("DataFolder", null);
		int spsl = config.getIntegerSetting("PatternStrSize", 128);
		
		for (int i = 1; i < maxSize; i++) {
        	String tidx = sf + "/index" + i;
        	FileIndexReader fir = new FileIndexReader(tidx, i + 1, spsl);
        
        	while (fir.next()) {
        		int insCnt = fir.getInstanceCount();
        		String ps = fir.getPatternString();
        			
       			pp.push(ps, codec.decodePattern(ps, true), insCnt);
        	}
        	fir.close();
        }
	}
	
	/**
	 * Check if a node can be extended
	 * @param pp
	 * @param sys
	 */
	private int extendConstraint(PatternPool pp, QuerySystem sys) {
		int tc = -1, psize = pp.getPatternSize();
		QueryGraph g = pp.getQueryGraph();

    	for (int i = psize - 1; i >= 0; i--)
    		if (g.getNode(i).isGeneral()) {
    			if (tc < 0) tc = 0;
    			System.out.println("Extending node constraints");
    			QueryGraphNode extNode = g.getNode(i);
    			
    			
    			Scan s = sys.queryPlanner().plan(getFullSchema(g)).open();
    			
    			int tmpcntr = 0;
    			
    			Map<Integer, HashCatContainer> nps = new HashMap<Integer, HashCatContainer>();
				
    			while (s.next()) {
    				
    				if ((++tmpcntr) % 50000 == 0)
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
    							hcc = new HashCatContainer(ng, pp.getExtendedSet());
    							
    							nps.put(lb, hcc);
    						}
    						
    						if (!hcc.checked) {
    							int[] ins = new int[psize];
    							for (int k = 0; k < psize; k++)
    								ins[hcc.cm.get(g.getNode(k))] = s.getID(g.getNode(k));

    							tc ++;
    							hcc.cntr ++;

    							fidx.addEntry(hcc.ps, psize, ins);
    						}
    					}
    				}
    			}
    			
    			s.close();
    			
    			for (Entry<Integer, HashCatContainer> e : nps.entrySet()) {
    				HashCatContainer hcc = e.getValue();
    				
 					pp.push(hcc.ps, hcc.graph, hcc.cntr);
    			}
    			
    			
    		}
    	return tc;
	}
	
	private int extendEdge(PatternPool pp, QuerySystem sys, EdgeInfo einfo) {
		List<Map<Edge, Integer>> list = new ArrayList<Map<Edge, Integer>>();
		QueryGraph g = pp.getQueryGraph();
		int psize = pp.getPatternSize();
		int tc = 0;
		
		System.out.println("Extending edges...");
		
		for (int i = 0; i < psize; i++)
			list.add(new HashMap<Edge, Integer>());
		
		Scan s = sys.queryPlanner().plan(getFullSchema(g)).open();
		
		while (s.next()) {
			for (int i = 0; i < psize; i++) {
				QueryGraphNode qn = g.getNode(i);
				int nid = s.getID(qn);
				List<Edge> edges = einfo.getEdges(nid); 
				Map<Edge, Integer> map = list.get(i);
				
				for (Edge e : edges) {
					Integer ti;
					
					if ((ti = map.get(e)) == null)
						ti = 0;
					else
						ti++;
					
					map.put(e, ti);
				}
			}
		}
		s.close();
		
		List<EdgeCountContainer> l = new ArrayList<EdgeCountContainer>();
		for (int i = 0; i < psize; i++) {
			for (Entry<Edge, Integer> e : list.get(i).entrySet()) {
				int insCnt = sys.patternManager().getPatternInstanceCount("*[+" + e.getKey().getLabel() + "::*]", 2);
				l.add(new EdgeCountContainer(i, e.getKey(), (double)e.getValue() / insCnt));
			}
		}
			
		Collections.sort(l);
			
		for (int i = 0; i < 5; i++) {
			Edge e = l.get(i).e;
			int toExt = l.get(i).toExt;
			
			System.out.println("\t" + e.toString() + " added to node " + g.getNode(toExt) + ".");
			QueryGraph nqs = GraphUtility.extendEdge(g, toExt, e.getLabel(), e.getDir(), true);

			int t = batchAddPattern(pp.getExtendedSet(), nqs, sys, (int)(coef * pp.getInstanceCount()));
			pp.push(sys.patternCodec().encodePattern(nqs), nqs, t);
			tc += t;
		}
		
		return tc;
	}
	
	/**
	 * 
	 * @param minJoinInsCnt
	 * @param totalThreshold
	 */
	public void indexComplexPatterns(String edgeInfo) {
		QuerySystem sys = new QuerySystem(config);
		PatternPool pp = new PatternPool(minJoinInsCnt);
		EdgeInfo einfo = new EdgeInfo(edgeInfo);

        int tc = 0;
        
        loadInitials(pp);


        while (pp.pop() && tc < totalThreshold) {
        	System.out.println(pp.getPatternString());
        	
        	int psize = pp.getPatternSize();
        	String pstr = pp.getPatternString();
        	
        	if (sys.patternManager().getPatternInstanceCount(pstr, psize) == null) {
        		sys.indexManager().close();
        		fidx.flush(psize);
        	}
        	
        	//Check if any nodes can be added constraints
        	
        	int t = extendConstraint(pp, sys);
        	
        	if (t != -1)
        		tc += t;        	
        	else
        		//If no node gets extended on constraints, we extend edges
        		if (extendEdge && psize < maxSize)
        			tc += extendEdge(pp, sys, einfo);
        }
        
        sys.indexManager().close();
        einfo.close();
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

		Plan p = sys.queryPlanner().plan(getFullSchema(graph));
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
	
	private QuerySchema getFullSchema(QueryGraph g) {
//		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
//		for (int j = g.nodeCount() - 1; j >= 0; j--)
//			nset.add(g.getNode(j));
		return new QuerySchema(g, g.getNodeSet());//nset);
	}

	static public void main(String[] args) {
		IterativeIndexer idx = new IterativeIndexer(new FileConfig(args[0]), 5000, 50000000);
		
		
//		idx.loadKeyword(args[2]);
		idx.indexComplexPatterns(args[1]);	
		idx.close();
	}
	
	class HashCatContainer {
		int cntr = 0;
		QueryGraph graph;
		Map<QueryGraphNode, Integer> cm;
		String ps;
		boolean checked;
		
		HashCatContainer(QueryGraph graph, Set<String> extended) {
			this.graph = graph;
			ps = codec.encodePattern(graph);
			cm = cnm.getMap(graph);
			this.checked = extended.contains(ps); 
		}
	}
	
	class EdgeCountContainer implements Comparable<Object>{
		int toExt;
		Edge e;
		double prop;
		
		EdgeCountContainer(int toExt, Edge e, double prop) {
			this.toExt = toExt;
			this.e = e;
			this.prop = prop;
		}

		@Override
		public int compareTo(Object arg0) {
			if (arg0 instanceof EdgeCountContainer)
				return Double.compare(prop, ((EdgeCountContainer) arg0).prop);
			else
				return 0;
		}
	}
}

//if (extendEdge && !tag && psize < maxSize) {
//System.out.println("Extending edges...");
//int labelCount = elabels.size(); 
//
//for (int toExt = psize - 1; toExt >= 0; toExt--) {
//	
//	for (int j = labelCount - 1; j >= 0; j --) {
//		String el = elabels.get(j);        				
//		
//		System.out.println("    Querying +Edge '" + el + "' ...");
//		
//		for (boolean dir = true; dir != false; dir = !dir) {
//			QueryGraph ng = GraphUtility.extendEdge(g, toExt, el, dir, true);        					
//			
//			int cnt = batchAddPattern(extended, ng, sys, (int)(hc.insCnt * coef));
//			
//			tc += cnt;
//			
//			if (cnt > minJoinInsCnt)
//				h.insert(new HeapContainer(codec.encodePattern(ng), ng, cnt));
//		}
//		
//	}
//}
//}

//BufferedReader rd;
//try {
//	rd = new BufferedReader(new FileReader(edges));
//	String temp;
//	while ((temp = rd.readLine()) != null)
//		elabels.add(temp);
//	rd.close();
//} catch (Exception e) {
//	e.printStackTrace();
//}

//public void indexComplexPatterns(int minJoinInsCnt, int totalThreshold, String edges) {
//List<String> elabels = new ArrayList<String>();