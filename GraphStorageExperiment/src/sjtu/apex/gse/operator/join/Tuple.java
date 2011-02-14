package sjtu.apex.gse.operator.join;

import java.util.Set;

public class Tuple {
	Integer[] values;
	Set<Integer> sources;
	
	public Tuple(Integer[] values, Set<Integer> sources) {
		this.values = values;
		this.sources = sources;
	}
	
	public int getValue(int id) {
		return values[id];
	}
	
	public Set<Integer> getSources() {
		return sources;
	}
}
