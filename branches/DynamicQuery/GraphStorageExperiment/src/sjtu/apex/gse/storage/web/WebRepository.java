package sjtu.apex.gse.storage.web;

import java.util.Set;

import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraphNode;

public class WebRepository implements Scan {

	public void addKey(String sub, String pred, String obj) {
		
	}
	
	public void keyEnded() {
		
	}
	
	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getID(QueryGraphNode n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Integer> getSourceSet() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get the binding of the given node in current entry.
	 * 
	 * @param nodeID 0 represents the subject node while 1 represents the object node.
	 */
	@Override
	public int getID(int nodeID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasNode(QueryGraphNode n) {
		// TODO Auto-generated method stub
		return false;
	}

}
