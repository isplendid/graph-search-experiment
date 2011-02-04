package sjtu.apex.gse.operator;

import java.util.Set;

import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
/**
 * The scan of select node
 * @author Tian Yuan
 *
 */
public class SelectScan implements Scan {
	Scan src;
	QuerySchema sch;
	
	public SelectScan(Scan src, QuerySchema sch) {
		this.src = src;
		this.sch = sch;
	}
	

	public void beforeFirst() {
		src.beforeFirst();
	}

	public void close() {
		src.close();
	}
	
	public boolean next() {
		return src.next();
	}

	public int getID(QueryGraphNode n) {
		return src.getID(n);
	}
	
	public int getID(int nodeID) {
		return src.getID(sch.getSelectedNode(nodeID));
	}

	public boolean hasNode(QueryGraphNode n) {
		return sch.hasNode(n);
	}


	@Override
	public Set<Integer> getSourceSet() {
		// TODO Auto-generated method stub
		return null;
	}	

}
