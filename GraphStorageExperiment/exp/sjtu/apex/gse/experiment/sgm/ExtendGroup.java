package sjtu.apex.gse.experiment.sgm;

import java.util.Map;

import sjtu.apex.gse.struct.QueryGraphNode;

public class ExtendGroup {
	Map<QueryGraphNode, QueryGraphNode> map;
	QueryGraphNode nn;
	
	public ExtendGroup(Map<QueryGraphNode, QueryGraphNode> map, QueryGraphNode nn) {
		this.map = map;
		this.nn = nn;
	}
	
	public Map<QueryGraphNode, QueryGraphNode> getMap() {
		return map;
	}
}