package sjtu.apex.gse.operator.join;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class HashJoinScan implements Scan {
	
	private static final int totalThread = 2;
	
	private BlockingQueue<Tuple> outputQueue;
	private Queue<Tuple> buffer;
	private Tuple currentEntry;
	private QuerySchema sch;
	private ThreadCounter threadCnt = new ThreadCounter();
	
	public HashJoinScan(Scan l, Scan r, List<Integer> lo, List<Integer> ro, QuerySchema sch) {
		
		this.sch = sch;
		this.buffer = new LinkedList<Tuple>();
		
		Map<QueryGraphNode, Integer> leftMapping = new HashMap<QueryGraphNode, Integer>();
		Map<QueryGraphNode, Integer> rightMapping = new HashMap<QueryGraphNode, Integer>();
		
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
		
		new ProducerThread(leftMerger, l, leftNodes, lo, threadCnt, totalThread).start();
		new ProducerThread(rightMerger, r, rightNodes, ro, threadCnt, totalThread).start();
	}

	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean next() {
		if (threadCnt.getEndedThreadCount() < totalThread) {
			if (buffer.size() <= 0) {
				try {
					buffer.add(outputQueue.take());
					outputQueue.drainTo(buffer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			currentEntry = buffer.remove();
			
			if (currentEntry.isDummy())
				return false;
			else
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

	private class ProducerThread extends Thread {
		private ResultMerger merger;
		private Scan src;
		private int rowWidth;
		private QueryGraphNode[] nodeList;
		private List<Integer> joinColumn;
		private ThreadCounter threadCnt;
		private int totalThread;
		
		public ProducerThread(ResultMerger merger, Scan src, QueryGraphNode[] nodeList, List<Integer> joinColumn, ThreadCounter threadCnt, int totalThread) {
			this.merger = merger;
			this.src = src;
			this.nodeList = nodeList;
			this.rowWidth = nodeList.length;
			this.threadCnt = threadCnt;
			this.totalThread = totalThread;
		}
		
		public void run() {
			while (src.next()) {
				int[] row = new int[rowWidth];
				for (int i = 0; i < rowWidth; i++) row[i] = src.getID(nodeList[i]);
				
				int[] joinValue = new int[joinColumn.size()];
				for (int i = 0; i < joinColumn.size(); i++) joinValue[i] = src.getID(joinColumn.get(i));
				
				merger.addTuple(joinValue, new Tuple(row, src.getSourceSet()));
			}
			threadCnt.threadEnded();
			if (threadCnt.getEndedThreadCount() >= totalThread)
				merger.addPoisonToken();
		}
	}

}
