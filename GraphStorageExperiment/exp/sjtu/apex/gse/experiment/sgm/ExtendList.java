package sjtu.apex.gse.experiment.sgm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sjtu.apex.gse.struct.QueryGraphNode;

public class ExtendList {
	private Map<String, List<ExtendGroup>> list;
	
	public ExtendList() {
		list = new HashMap<String, List<ExtendGroup>>();
	}
	
	public void add(String label, Map<QueryGraphNode, QueryGraphNode> map, QueryGraphNode target) {
		List<ExtendGroup> el = list.get(label);
		
		if (el == null) {
			el = new ArrayList<ExtendGroup>();
			list.put(label, el);
		}
		
		el.add(new ExtendGroup(map, target));
	}
	
	public Set<String> getAllLabel() {
		return list.keySet();
	}
	
	public List<ExtendGroup> getGroups(String label) {
		return list.get(label);
	}

}
