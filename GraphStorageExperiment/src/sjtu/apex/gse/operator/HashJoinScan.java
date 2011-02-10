package sjtu.apex.gse.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class HashJoinScan implements Scan {
	
	private Map<QueryGraphNode, Integer> leftMapping;
	private Map<QueryGraphNode, Integer> rightMapping;
	private BlockingQueue<Tuple> outputQueue;
	private Tuple currentEntry;
	private QuerySchema sch;
	private ThreadCounter threadCnt = new ThreadCounter();
	
	public HashJoinScan(Scan l, Scan r, List<Integer> lo, List<Integer> ro, QuerySchema sch) {
		
		this.sch = sch;
		leftMapping = new HashMap<QueryGraphNode, Integer>();
		rightMapping = new HashMap<QueryGraphNode, Integer>();
		
		for (int i = 0; i < sch.getSelectedNodeCount(); i++) {
			QueryGraphNode n = sch.getSelectedNode(i);
			
			if (l.hasNode(n)) {
				leftMapping.put(n, leftMapping.size());
			}
			else {
				rightMapping.put(n, rightMapping.size());
			}
		}
		
		QueryGraphNode[] leftNodes = new QueryGraphNode[leftMapping.size()];
		QueryGraphNode[] rightNodes = new QueryGraphNode[rightMapping.size()];
		
		
		for (Entry<QueryGraphNode, Integer> i : leftMapping.entrySet())
			leftNodes[i.getValue()] = i.getKey();
		
		for (Entry<QueryGraphNode, Integer> i : rightMapping.entrySet())
			rightNodes[i.getValue()] = i.getKey();
		
		outputQueue = new LinkedBlockingQueue<Tuple>();
		
		ResultMerger leftMerger = new ResultMerger(outputQueue, sch, leftMapping);
		ResultMerger rightMerger = new ResultMerger(outputQueue, sch, rightMapping);
		
		leftMerger.setJoiningMerger(rightMerger);
		rightMerger.setJoiningMerger(leftMerger);
		
		new ProducerThread(leftMerger, l, leftNodes, lo, threadCnt).run();
		new ProducerThread(rightMerger, r, rightNodes, ro, threadCnt).run();
	}

	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean next() {
		if (threadCnt.getEndedThreadCount() < 2) {
			try {
				currentEntry = outputQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		else
			return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID(QueryGraphNode n) {
		return currentEntry.getValue(sch.getNodeID(n));
	}

	@Override
	public Set<Integer> getSourceSet() {
		return currentEntry.getSources();
	}

	@Override
	public int getID(int nodeID) {
		return currentEntry.getValue(nodeID);
	}

	@Override
	public boolean hasNode(QueryGraphNode n) {
		return sch.hasNode(n);
	}
	
	private class ThreadCounter {
		private int endedThreadCount = 0;
		
		public synchronized int getEndedThreadCount() {
			return this.endedThreadCount;
		}
		
		public synchronized void threadEnded() {
			this.endedThreadCount ++;
		}
	}

	private class ProducerThread extends Thread {
		private ResultMerger merger;
		private Scan src;
		private int rowWidth;
		private QueryGraphNode[] nodeList;
		private List<Integer> joinColumn;
		private ThreadCounter threadCnt;
		
		public ProducerThread(ResultMerger merger, Scan src, QueryGraphNode[] nodeList, List<Integer> joinColumn, ThreadCounter threadCnt) {
			this.merger = merger;
			this.src = src;
			this.nodeList = nodeList;
			this.rowWidth = nodeList.length;
		}
		
		public void run() {
			while (src.next()) {
				Integer[] row = new Integer[rowWidth];
				for (int i = 0; i < rowWidth; i++) row[i] = src.getID(nodeList[i]);
				
				Integer[] joinValue = new Integer[joinColumn.size()];
				for (int i = 0; i < joinColumn.size(); i++) joinValue[i] = src.getID(joinColumn.get(i));
				
				merger.addTuple(joinValue, new Tuple(row, src.getSourceSet()));
			}
			threadCnt.threadEnded();
		}
	}
	
	private class Tuple {
		Integer[] values;
		Set<Integer> sources;
		
		public Tuple(Integer[] values, Set<Integer> sources) {
			this.values = values;
			this.sources = sources;
		}
		
		public int getValue(int id) {
			return values[id];
		}
		
		public Set<Integer> getSources() {
			return sources;
		}
	}
	
	private class ResultMerger {
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
			Integer[] values = new Integer[sch.getSelectedNodeCount()];
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
		
		public synchronized void addTuple(Integer[] joinValue, Tuple tuple) {
			ArrayHashKey key = new ArrayHashKey(joinValue);
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
		
		public synchronized List<Tuple> getTuples(ArrayHashKey key) {
			return new ArrayList<Tuple>(map.get(key));
		}
		
		public Map<QueryGraphNode, Integer> getNodeMapping() {
			return nmThis;
		}
	}
	
	private class ArrayHashKey {
		private Integer[] joinValue;
		
		public ArrayHashKey(Integer[] joinValue) {
			this.joinValue = joinValue;
		}
		
		@Override
		public int hashCode() {
			int h = 0;
			
			for (int ki : joinValue) {
				int highorder = h & 0xf8000000;
				h = h << 5;
				h = h ^ (highorder >> 27);
				h = h ^ ki;
			}
			
			return h;
		}
		
		@Override
		public boolean equals(Object o) {
			boolean ret = false;
			
			if (o instanceof ArrayHashKey) {
				 Integer[] oValues = ((ArrayHashKey) o).joinValue;
				 
				 if (oValues.length == joinValue.length) {
					 ret = true;
					 
					 for (int i = 0; i < oValues.length; i++)
						 if (oValues[i] != joinValue[i])
							 ret = false;
				 }
			}
			
			return ret;
		}
	}
}
