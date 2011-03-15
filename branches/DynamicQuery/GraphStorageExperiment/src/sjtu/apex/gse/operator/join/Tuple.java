package sjtu.apex.gse.operator.join;

import java.util.Set;

public class Tuple {
	int[] values;
	Set<Integer> sources;
	
	/**
	 * Create a dummy tuple without information
	 */
	public Tuple() {
		this.values = null;
		this.sources = null;
	}
	
	/**
	 * Create a tuple
	 * 
	 * @param values - The array holding the IDs of the bindings
	 * @param sources - The set of sources relavent to this tuple
	 */
	public Tuple(int[] values, Set<Integer> sources) {
		this.values = values;
		this.sources = sources;
	}
	
	public int getValue(int id) {
		return values[id];
	}
	
	public Set<Integer> getSources() {
		return sources;
	}
	
	/**
	 * Get the dumminess of this tuple
	 * @return true - if the tuple is dummy; false - otherwise.
	 */
	public boolean isDummy() {
		return values == null;
	}
}
