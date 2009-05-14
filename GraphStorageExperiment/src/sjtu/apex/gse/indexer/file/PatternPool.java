package sjtu.apex.gse.indexer.file;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.util.Heap;

public class PatternPool {
	private Set<String> extended;
	private Heap h;
	private HeapContainer hc;
	private QueryGraph g;
	private int gSize, minJoinInsCnt;
	
	public PatternPool(int minJoinInsCnt) {
		h = new Heap();
		extended = new HashSet<String>();
		this.minJoinInsCnt = minJoinInsCnt; 
	}
	
	public boolean pop() {
		if (h.size() == 0)
			return false;
		
		hc = (HeapContainer)h.remove();
		g = hc.graph;
		gSize = g.nodeCount();
		return true;
	}
	
	public QueryGraph getQueryGraph() {
		return g;
	}
	
	public int getPatternSize() {
		return gSize;
	}
	
	public String getPatternString() {
		return hc.patternStr;
	}
	
	public int getInstanceCount() {
		return hc.insCnt;
	}
	
	public void push(String ps, QueryGraph g, int insCnt) {
		if (!extended.contains(ps)) {
			if (insCnt > minJoinInsCnt)
				h.insert(new HeapContainer(ps, g, insCnt));
			extended.add(ps);
		}
		
	}
	
	public Set<String> getExtendedSet() {
		return extended;
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
