package sjtu.apex.gse.operator.join;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sun.security.util.Debug;

public class ResultMerger {
	ResultMerger joining;
	BlockingQueue<Tuple> output;
	Map<ArrayHashKey, List<Tuple>> map;
	Map<QueryGraphNode, Integer> nmThis;
	Map<QueryGraphNode, Integer> nmThat;
	QuerySchema sch;
	
	public ResultMerger(BlockingQueue<Tuple> output, QuerySchema schema, Map<QueryGraphNode, Integer> nodeMapping) {
		this.output = output;
		map = new HashMap<ArrayHashKey, List<Tuple>>();
		this.sch = schema;
		this.nmThis = nodeMapping;
	}
	
	public void setJoiningMerger(ResultMerger joiningMerger) {
		this.joining = joiningMerger;
		this.nmThat = joiningMerger.getNodeMapping();
	}
	
	private Tuple mergeTuples(Tuple from, Tuple to) {
		int[] values = new int[sch.getSelectedNodeCount()];
		Set<Integer> sources = new HashSet<Integer>();
		
		for (int i = 0; i < sch.getSelectedNodeCount(); i++) {
			QueryGraphNode node = sch.getSelectedNode(i);
			
			if (nmThis.containsKey(node))
				values[i] = from.getValue(nmThis.get(node)); 
			else
				values[i] = to.getValue(nmThat.get(node));
		}
		
		sources.addAll(from.getSources());
		sources.addAll(to.getSources());
		
		return new Tuple(values, sources);
	}
	
	public synchronized void addTuple(int[] joinValue, Tuple tuple) {
		ArrayHashKey key = new ArrayHashKey(joinValue);
		Debug.println("addTuple", "jv = " + joinValue[0] + " by " + this.toString());
		List<Tuple> value;
		
		if (map.containsKey(key))
			value = map.get(key);
		else {
			value = new ArrayList<Tuple>();
			map.put(key, value);
		}
		
		value.add(tuple);
		
		for (Tuple t : joining.getTuples(key)) {
			Tuple o = mergeTuples(tuple, t);
			
			output.add(o);
		}
	}
	
	public synchronized void addPoisonToken() {
		output.add(new Tuple());
	}
	
	private List<Tuple> getTuples(ArrayHashKey key) {
		List<Tuple> ret = map.get(key);
		if (ret == null)
			ret = new ArrayList<Tuple>();
		else
			ret = new ArrayList<Tuple>(ret);
		
		return ret;
	}
	
	public Map<QueryGraphNode, Integer> getNodeMapping() {
		return nmThis;
	}
}
