package sjtu.apex.gse.operator;

import java.util.Map;

import sjtu.apex.gse.storage.file.FileRepository;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


public class PatternScan implements Scan{
	
//	private static String tableName = "TRIPLE"; 
	
	private QuerySchema sch;
	private Map<QueryGraphNode, Integer> cmap;
	private Scan src;
	
//	private String getTableName(int size) {
//		return tableName + size;
//	}
	
	public PatternScan(QuerySchema sch, Map<QueryGraphNode, Integer> cmap, String ps) {
		this.sch = sch;
		this.cmap = cmap;
		
		src = new FileRepository(ps, sch.getQueryGraph().nodeCount());
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < sch.getSelectedNodeCount(); i++) {
//			QueryGraphNode n = sch.getSelectedNode(i);
//			
//			sb.append("NODE" + cmap.get(n) + " ");
//		}
//		src = new DBRepository("SELECT " + sb + " FROM " + getTableName(sch.getQueryGraph().nodeCount()) + " WHERE PATTERN = '" + ps + "'", cmap);
		
	}

	public void beforeFirst() {
		src.beforeFirst();
	}

	public void close() {
		src.close();		
	}

	public int getID(QueryGraphNode n) {
		return src.getID(cmap.get(n));
	}
	
	public int getID(int nodeID) {
		return src.getID(cmap.get(sch.getSelectedNode(nodeID)));
	}

	public boolean next() {
		return src.next();
	}

	public boolean hasNode(QueryGraphNode n) {
		return sch.hasNode(n);
	}

}
 