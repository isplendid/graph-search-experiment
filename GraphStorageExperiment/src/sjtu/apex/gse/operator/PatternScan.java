package sjtu.apex.gse.operator;

import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.storage.file.FileRepository;
import sjtu.apex.gse.storage.file.SourceHeapReader;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;


public class PatternScan implements Scan{
	
	private QuerySchema sch;
	private Map<QueryGraphNode, Integer> cmap;
	private Scan src;
	
	
	public PatternScan(QuerySchema sch, QuerySystem qs, Map<QueryGraphNode, Integer> cmap, String ps) {
		this.sch = sch;
		this.cmap = cmap;
		int size = sch.getQueryGraph().nodeCount();
		src = new FileRepository(qs.indexManager(), qs.workingDirectory()  + "/storage" + (size - 1), ps, size, new SourceHeapReader(qs.workingDirectory() + "/srcheap"));
		
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

	@Override
	public Set<Integer> getSourceSet() {
		return src.getSourceSet();
	}

}
 