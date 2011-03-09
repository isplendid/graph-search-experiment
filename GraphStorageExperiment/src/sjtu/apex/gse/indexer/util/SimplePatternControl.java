package sjtu.apex.gse.indexer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.struct.QueryGraph;

public class SimplePatternControl {
	Map<Integer, Map<Pair, QueryGraph>> contain;
	ModHash mod;
	
	public SimplePatternControl(ModHash hash) {
		this.mod = hash;
		contain = new HashMap<Integer, Map<Pair, QueryGraph>>();
	}
	
	public void addPattern(QueryGraph g) {
		int elabel = g.getEdge(0).getLabel();
		
		Map<Pair, QueryGraph> ps = contain.get(elabel);
		if (ps == null) {
			ps = new HashMap<Pair, QueryGraph>();
			contain.put(elabel, ps);
		}
		
		Pair np = new Pair(g.getEdge(0).getNodeFrom().getHashLabel(mod), g.getEdge(0).getNodeTo().getHashLabel(mod));
		if (!ps.containsKey(np))
			ps.put(np, g);
		else
			System.err.println("error");
	}
	
	public Set<Integer> getLabelSet() {
		return new HashSet<Integer>(contain.keySet());
	}
	
	public Set<Pair> getPairs(String label) {
		return new HashSet<Pair>(contain.get(label).keySet());
	}
	
	public boolean containsEdge(String label) {
		return contain.get(label) != null;
	}
	
	public boolean containsPattern(String label, int shash, int ohash) {
		Map<Pair, QueryGraph> set = contain.get(label);
		if (set == null)
			return false;
		else
			return set.get(new Pair(shash, ohash)) != null;
	}
	
	public List<QueryGraph> getAvailPatterns(String label, int[] sl, int[] ol) {
		List<QueryGraph> res = new ArrayList<QueryGraph>();
		int modulo = mod.getModulo();
		boolean[] sbuck = new boolean[modulo * 2];
		boolean[] obuck = new boolean[modulo * 2];
		
		for (int s : sl) 
			sbuck[mod.hashInt(s) + modulo] = true;
		
		for (int s : ol) 
			obuck[mod.hashInt(s) + modulo] = true;
		
		for (Entry<Pair, QueryGraph> e : contain.get(label).entrySet()) {
			int ss = e.getKey().s;
			int os = e.getKey().o;
			
			if ((ss < 0 || sbuck[ss + modulo]) &&
					(os < 0 || obuck[os + modulo]))
				res.add(e.getValue());
		}
		return res;
	}
	
	public class Pair {
		int s, o;
		
		public Pair(int s, int o) {
			this.s = s;
			this.o = o;
		}
		
		public int hashCode() {
			return (s + "::" + o).hashCode();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Pair)
				
				return (((Pair) o).s == this.s && ((Pair) o).o == this.o);
			else
				return false;
		}
		
		public int getSub() {
			return s;
		}
		
		public int getObj() {
			return o;
		}
	}
	
}
