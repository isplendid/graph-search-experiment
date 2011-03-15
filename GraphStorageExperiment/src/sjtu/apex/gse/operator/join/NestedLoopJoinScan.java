package sjtu.apex.gse.operator.join;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.operator.web.WebPatternScan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class NestedLoopJoinScan implements Scan {
	
	private final static int totalThread = 1;
	
	private BlockingQueue<Tuple> outputQueue;
	private Scan isrc;
	private WebPatternScan esrc;
	private QuerySchema sch;
	private ThreadCounter threadCnt;
	private Tuple currentEntry;
	
	public NestedLoopJoinScan(Scan isrc, WebPatternScan esrc, List<Integer> lo, List<Integer> ro, QuerySchema sch, QueryGraphNode subject, QueryGraphNode object) {
		this.outputQueue = new LinkedBlockingQueue<Tuple>();
		this.isrc = isrc;
		this.esrc = esrc;
		this.sch = sch;
		this.threadCnt = new ThreadCounter();
		
		Map<QueryGraphNode, Integer> leftMapping = new HashMap<QueryGraphNode, Integer>();
		Map<QueryGraphNode, Integer> rightMapping = new HashMap<QueryGraphNode, Integer>();
		
		for (int i = 0; i < sch.getSelectedNodeCount(); i++) {
			QueryGraphNode n = sch.getSelectedNode(i);
			
			if (isrc.hasNode(n)) {
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
		
		ResultMerger mergerLeft = new ResultMerger(outputQueue, sch, leftMapping);
		ResultMerger mergerRight = new ResultMerger(outputQueue, sch, rightMapping);
		
		mergerLeft.setJoiningMerger(mergerRight);
		mergerRight.setJoiningMerger(mergerLeft);
		
		new KeyRegistrationThread(isrc, esrc, lo, mergerLeft, leftNodes, subject, object).start();
		new JoinThread(esrc, ro, mergerRight, rightNodes, threadCnt).start();
	}

	@Override
	public void beforeFirst() {
		esrc.beforeFirst();
	}

	@Override
	public boolean next() {
		if (threadCnt.getEndedThreadCount() < totalThread) {
			try {
				currentEntry = outputQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
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
		isrc.close();
		esrc.close();
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
	
	private class KeyRegistrationThread extends Thread {
		Scan src;
		WebPatternScan tar;
		ResultMerger merger;
		QueryGraphNode[] nodeList;
		QueryGraphNode subject, object;
		List<Integer> joinColumn;
		int rowWidth;
		
		public KeyRegistrationThread(Scan src, WebPatternScan tar, List<Integer> joinColumn, ResultMerger merger, QueryGraphNode[] nodeList, QueryGraphNode subject, QueryGraphNode object) {
			this.src = src;
			this.tar = tar;
			this.merger = merger;
			this.nodeList = nodeList;
			this.rowWidth = nodeList.length;
			this.joinColumn = joinColumn;
			this.object = object;
			this.subject = subject;
		}
		
		public void run() {
			int sub, obj;
			
			while (src.next()) {
				Integer[] row = new Integer[rowWidth];
				for (int i = 0; i < rowWidth; i++) row[i] = src.getID(nodeList[i]);
				
				Integer[] joinValue = new Integer[joinColumn.size()];
				for (int i = 0; i < joinColumn.size(); i++) joinValue[i] = src.getID(joinColumn.get(i));
				
				merger.addTuple(joinValue, new Tuple(row, src.getSourceSet()));
				
				//TODO Handle nodes representing resources here
				sub = (src.hasNode(subject) ? src.getID(subject) : -1);
				obj = (src.hasNode(object) ? src.getID(object) : -1);
				tar.addKey(sub, obj, new HashSet<Integer>(0));
			}
			tar.keyEnded();
		}
	}
	
	private class JoinThread extends Thread {
		WebPatternScan src;
		ResultMerger merger;
		List<Integer> joinColumn;
		int rowWidth;
		QueryGraphNode[] nodeList;
		ThreadCounter counter;
	
		public JoinThread(WebPatternScan src, List<Integer> joinColumn, ResultMerger merger, QueryGraphNode[] nodeList, ThreadCounter counter) {
			this.src = src;
			this.merger = merger;
			this.joinColumn = joinColumn;
			this.rowWidth = nodeList.length;
			this.nodeList = nodeList;
			this.counter = counter;
		}
		
		public void run() {
			while (src.next()) {
				Integer[] row = new Integer[rowWidth];
				for (int i = 0; i < rowWidth; i++) row[i] = src.getID(nodeList[i]);
				
				Integer[] joinValue = new Integer[joinColumn.size()];
				for (int i = 0; i < joinColumn.size(); i++) joinValue[i] = src.getID(joinColumn.get(i));
				
				merger.addTuple(joinValue, new Tuple(row, src.getSourceSet()));
			}
			counter.threadEnded();
			merger.addPoisonToken();
		}
	}
}
