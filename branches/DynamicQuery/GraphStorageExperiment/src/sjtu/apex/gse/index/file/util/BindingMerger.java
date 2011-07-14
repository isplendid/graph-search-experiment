package sjtu.apex.gse.index.file.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.operator.join.ArrayHashKey;
import sjtu.apex.gse.operator.join.Tuple;

public class BindingMerger {
	private HashMap<ArrayHashKey, Tuple> keyEntryMap;
	private HashSet<Integer> relevantSrc;
	
	/**
	 * 
	 */
	public BindingMerger() {
		this.keyEntryMap = new HashMap<ArrayHashKey, Tuple>();
		this.relevantSrc = new HashSet<Integer>();
	}
	
	public void AddEntry(int[] bindings, Set<Integer> sources) {
		ArrayHashKey key = new ArrayHashKey(bindings);
		Tuple tmpEntry;
		
		if (!keyEntryMap.containsKey(key)) {
			tmpEntry = new Tuple(bindings, new HashSet<Integer>());
			keyEntryMap.put(key, tmpEntry);
		}
		else
			tmpEntry = keyEntryMap.get(key);

		for (Integer i : sources) {
			tmpEntry.getSources().add(i);
			relevantSrc.add(i);
		}
	}
	
	public boolean isEmpty() {
		return (keyEntryMap.size() == 0);
	}
	
	public Collection<Tuple> getTuples() {
		return keyEntryMap.values();
	}
	
	public Set<Integer> getRelevantSources() {
		return relevantSrc;
	}
}
