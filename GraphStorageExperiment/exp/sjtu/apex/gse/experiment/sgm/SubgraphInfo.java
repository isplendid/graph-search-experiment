package sjtu.apex.gse.experiment.sgm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;

public class SubgraphInfo implements Comparable<Object> {
	private QueryGraph model;
	private List<Map<QueryGraphNode, QueryGraphNode>> mapping;
	
	public SubgraphInfo(QueryGraph model) {
		mapping = new ArrayList<Map<QueryGraphNode, QueryGraphNode>>();
		this.model = model;
	}
	
	public int getInstanceCount() {
		return mapping.size();
	}
	
	public QueryGraph getPattern() {
		return model;
	}
	
	public void addMap(Map<QueryGraphNode, QueryGraphNode> nm) {
		mapping.add(nm);
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof SubgraphInfo)
			return getInstanceCount() - ((SubgraphInfo) arg0).getInstanceCount();
		else
			return 0;
	}
}
