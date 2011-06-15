package sjtu.apex.gse.operator.web;

import ie.deri.urq.lidaq.repos.StringKeyObserver;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.operator.join.ArrayHashKey;
import sjtu.apex.gse.operator.join.Tuple;
import sun.security.util.Debug;

public class KeyObserverImpl extends StringKeyObserver {
	WebPatternScan src;
	HashSet<ArrayHashKey> uniqueSet;

	public KeyObserverImpl(WebPatternScan src) {
		super();
		this.src = src;
		this.uniqueSet = new HashSet<ArrayHashKey>();
	}

	@Override
	protected void receiveStatement(String[] statement) {
		if (statement == null) {
			src.systemIdled();
		}
		else {
			int[] values = new int[2];
			int subID = src.idman.getID(statement[0]);
			int objID = src.idman.getID(statement[2]);
			
			if (subID < 0) subID = src.idman.addURI(statement[0]);
			if (objID < 0) objID = src.idman.addURI(statement[2]);
			
			values[0] = subID;
			values[1] = objID;
			
			ArrayHashKey key = new ArrayHashKey(values);
			
			if (!uniqueSet.contains(key)) {
				uniqueSet.add(key);
			
				int srcID = src.srcman.getID(statement[3]);
				if (srcID < 0) srcID = src.srcman.addSource(statement[3]);
				
				Set<Integer> sources = new HashSet<Integer>();
				sources.add(srcID);
				
				Debug.println("receiveStatement", "p = " + statement[1] + " , " + subID + " = " + statement[0] + " , " + objID + " = " + statement[2]);
				src.addResult(new Tuple(values, sources));
			}
		}
	}

}
