package sjtu.apex.gse.operator.web;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.SourceManager;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.operator.join.Tuple;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sun.security.util.Debug;

public class WebPatternScan implements Scan {
	
	private BlockingQueue<Tuple> output;
	private ie.deri.urq.lidaq.repos.WebRepository src;
	private QueryGraphNode subNode;
	private QueryGraphNode objNode;
	private String pred;
	private Tuple currentEntry;
	private boolean keyEnded;
	private boolean lastFetched;
	protected boolean srcIdled;
	private boolean hasKey;
	
	protected IDManager idman;
	protected SourceManager srcman;
	
	public WebPatternScan(QuerySchema sch, IDManager idman, SourceManager srcman, ie.deri.urq.lidaq.repos.WebRepository webRepos) {
		this(sch.getQueryGraph().getEdge(0).getNodeFrom(), sch.getQueryGraph().getEdge(0).getLabel(), sch.getQueryGraph().getEdge(0).getNodeTo(), idman, srcman, webRepos);
	}
	
	public WebPatternScan(QueryGraphNode sub, int pred, QueryGraphNode obj, IDManager idman, SourceManager srcman, ie.deri.urq.lidaq.repos.WebRepository webRepos) {
		this.src = webRepos;
		this.output = new LinkedBlockingQueue<Tuple>();
		this.pred = idman.getURI(pred);
		this.subNode = sub;
		this.objNode = obj;
		this.idman = idman;
		this.keyEnded = false;
		this.srcman = srcman;
		this.srcIdled = true;
		this.lastFetched = false;
	}
	
	private String getLabelFromID(int id) {
		if (id < 0)
			return "*";
		else
			return idman.getURI(id);
	}

	/**
	 * Listen to the result of a given key
	 * 
	 * @param sub - The binding of the subject node
	 * @param obj - The binding of the object node
	 * @param sources - The set of sources
	 */
	public void addKey(int sub, int obj, Set<Integer> sources) {
		String subStr = sub < 0 ? getLabelFromID(subNode.getLabel()) : idman.getURI(sub);
		String objStr = obj < 0 ? getLabelFromID(objNode.getLabel()) : idman.getURI(obj);
		
		if (subStr == "*") subStr = "?s";
		if (objStr == "*") objStr = "?o";
			
		String[] key = {subStr, pred, objStr};
		
		
		Debug.println("addKey", key[0] + " " + key[1] + " " + key[2]);
		src.addObserver(new KeyObserverImpl(this, key, convertSrcSetToExternal(sources)));
		hasKey = true;
	}
	
	private synchronized boolean hasThreadWaitingOnNext() {
		return keyEnded && !lastFetched;// && output.size() <= 0;
	}
	
	private synchronized void setKeyEnded(boolean value) {
		keyEnded = value;
	}
	
	/**
	 * Indicate there is no more key that will be added to the listener's list
	 */
	public void keyEnded() {
		Debug.println("keyEnded", " called by " + pred);
		setKeyEnded(true);
		
		if (!hasKey)
			output.add(new Tuple());
	}
	
	protected void systemIdled() {
		if (hasThreadWaitingOnNext()) {
			Debug.println("systemIdled", "called by prdicate " + pred + " , keyEnded = " + keyEnded + " , output size = " + output.size());
			output.add(new Tuple());
		}
	}
	
	protected void addResult(Tuple t) {
		Debug.println("addResult", t.toString());
		if (!lastFetched)
			output.add(t);
		else
			throw new AddResultAfterIteratorFinishException();
	}
	
	private Set<String> convertSrcSetToExternal(Set<Integer> sources) {
		Set<String> ret = new HashSet<String>();
		
		for (Integer id : sources)
			ret.add(srcman.getSource(id));
		
		return ret;
	}
	
	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean next() {
		boolean ret;
	
		try {
			currentEntry = output.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (currentEntry.isDummy()) {
			ret = false;
			lastFetched = true;
		}
		else
			ret = true;
		
		return ret;
	}

	@Override
	public void close() {
//		src.shutdown();

	}

	@Override
	public int getID(QueryGraphNode n) {
		if (n.equals(subNode))
			return currentEntry.getValue(0);
		else if (n.equals(objNode))
			return currentEntry.getValue(1);
		else
			return -1;
	}

	@Override
	public Set<Integer> getSourceSet() {
		return currentEntry.getSources();
	}

	/**
	 * Get the binding of the given node in current entry.
	 * 
	 * @param nodeID 0 represents the subject node while 1 represents the object node.
	 */
	@Override
	public int getID(int nodeID) {
		if (nodeID >=0 && nodeID < 3)
			return currentEntry.getValue(nodeID);
		else
			return -1;
	}

	@Override
	public boolean hasNode(QueryGraphNode n) {
		return (n.equals(subNode) || n.equals(objNode));
	}

	public class AddResultAfterIteratorFinishException extends RuntimeException{
		
	}
}
