package sjtu.apex.gse.indexer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.struct.QueryGraph;

public class SimplePatternControl {
	Map<String, Map<Pair, QueryGraph>> contain;
	ModHash mod;
	
	public SimplePatternControl(ModHash hash) {
		this.mod = hash;
		contain = new HashMap<String, Map<Pair, QueryGraph>>();
	}
	
	public void addPattern(QueryGraph g) {
		String elabel = g.getEdge(0).getLabel();
		
		Map<Pair, QueryGraph> ps = contain.get(elabel);
		if (ps == null) {
			ps = new HashMap<Pair, QueryGraph>();
			contain.put(elabel, ps);
		}
		
		Pair np = new Pair(g.getEdge(0).getNodeFrom().getLabel(), g.getEdge(0).getNodeFrom().getLabel());
		ps.put(np, g);
	}
	
	public Set<String> getLabelSet() {
		return contain.keySet();
	}
	
	public boolean containsEdge(String label) {
		return contain.get(label) != null;
	}
	
	public boolean containsPattern(String label, String shash, String ohash) {
		Map<Pair, QueryGraph> set = contain.get(label);
		if (set == null)
			return false;
		else
			return set.get(new Pair(shash, ohash)) != null;
	}
	
	public List<QueryGraph> getAvailPatterns(String label, String[] sl, String[] ol) {
		List<QueryGraph> res = new ArrayList<QueryGraph>();
		boolean[] sbuck = new boolean[mod.getModulo() * 2];
		boolean[] obuck = new boolean[mod.getModulo() * 2];
		
		for (String s : sl) 
			sbuck[mod.hashInt(s)] = true;
		
		for (String s : ol) 
			obuck[mod.hashInt(s)] = true;
		
		for (Entry<Pair, QueryGraph> e : contain.get(label).entrySet()) {
			String ss = e.getKey().s;
			String os = e.getKey().o;
			
			if ((ss.equals("*") || sbuck[Integer.parseInt(ss)]) &&
					(os.equals("*") || obuck[Integer.parseInt(os)]))
				res.add(e.getValue());
		}
		return res;
	}
	
	class Pair {
		String s, o;
		
		public Pair(String s, String o) {
			this.s = s;
			this.o = o;
		}
		
		public int hashCode() {
			return (s + "::" + o).hashCode();
		}
	}
	
}
