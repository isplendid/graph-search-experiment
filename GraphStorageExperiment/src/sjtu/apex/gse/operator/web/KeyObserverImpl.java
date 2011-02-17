package sjtu.apex.gse.operator.web;

import ie.deri.urq.lidaq.repos.KeyObserver;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.operator.join.Tuple;

public class KeyObserverImpl extends KeyObserver {
	WebPatternScan src;

	public KeyObserverImpl(WebPatternScan src, String[] key, Set<String> sources) {
		super(key, sources);
		this.src = src;
	}

	@Override
	protected void receiveStatement(String[] statement) {
		if (statement == null)
			src.systemIdled();
			
		Integer[] values = new Integer[2];
		int subID = src.idman.getID(statement[0]);
		int objID = src.idman.getID(statement[2]);
		int srcID = src.srcman.getID(statement[3]);
		
		Set<Integer> sources = new HashSet<Integer>();
		
		
		if (subID < 0) subID = src.idman.addURI(statement[0]);
		if (objID < 0) objID = src.idman.addURI(statement[2]);
		if (srcID < 0) srcID = src.srcman.addSource(statement[3]);
		
		values[0] = subID;
		values[1] = objID;
		
		sources.add(srcID);
		
		src.addResult(new Tuple(values, sources));
	}

}
