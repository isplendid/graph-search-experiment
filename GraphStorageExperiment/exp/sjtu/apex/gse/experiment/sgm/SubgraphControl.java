package sjtu.apex.gse.experiment.sgm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.struct.QueryGraph;

public class SubgraphControl {
	Map<String, SubgraphInfo> maps;
	
	public SubgraphControl() {
		maps = new HashMap<String, SubgraphInfo>();
	}
	
	public SubgraphInfo getInfo(QueryGraph g, PatternCodec codec) {
		String ps = codec.encodePattern(g);
		SubgraphInfo si = maps.get(ps);
		if (si == null) {
			si = new SubgraphInfo(g);
			maps.put(ps, si);
		}
		
		return si;
	}
	
	public SubgraphInfo getSubgraphInfo(String ps) {
		return maps.get(ps);
	}
	
	public Set<String> getAllPatterns() {
		return maps.keySet();
	}
	
	public Collection<SubgraphInfo> getAllInfo() {
		return maps.values();
	}
}
