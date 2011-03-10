package sjtu.apex.gse.storage;

import java.util.Set;

import sjtu.apex.gse.operator.UpdateScan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


/**
 * A temporary table in RAM that stores the mediate results
 * 
 * Caution : Contents are held within an array. 
 * Sequential inserts and deletes are fast. Random inserts and deletes, however,
 * are slow due to arraycopy operations.
 * 
 * @author Tian Yuan
 *
 */
public class RAMArrayRepository implements UpdateScan {
	
	final static int maximumDoubleExt = 65536;
	
	QuerySchema qs;
	int[][] data;
	Set<Integer> sources[];
	int pointer, savedPosition = -1;
	int entryCnt;
	
	public RAMArrayRepository(QuerySchema qs) {
		this(qs, 1024);
	}
	
	public RAMArrayRepository(QuerySchema qs, int initSize) {
		data = new int[qs.getSelectedNodeCount()][];
		for (int i = 0; i < qs.getSelectedNodeCount(); i++)
			data[i] = new int[initSize];
		sources = new Set[initSize];
		this.qs = qs;
		pointer = -1;
		entryCnt = 0;
	}

	public void delete() {
		if (pointer < entryCnt - 1) {
			for (int i = 0; i < qs.getSelectedNodeCount(); i++)
				System.arraycopy(data[i], pointer + 1, data[i], pointer, data.length - pointer - 1);
		}
		entryCnt--;
		pointer--;

	}

	public void insert() {
		entryCnt++;
		if (entryCnt > data[0].length)
			for (int i = 0; i < qs.getSelectedNodeCount(); i++){
				int newSize;
				
				if (data[i].length < maximumDoubleExt)
					newSize = data[i].length * 2;
				else
					newSize = data[i].length + 32768;
				
				int[] newData = new int[newSize];
				Set<Integer>[] newSource = new Set[newSize];
				
				System.arraycopy(data[i], 0, newData, 0, pointer + 1);
				System.arraycopy(data[i], pointer + 1, newData, pointer + 2, entryCnt - 2 - pointer);
				data[i] = newData;
				
				System.arraycopy(sources, 0, newSource, 0, pointer + 1);
				System.arraycopy(sources, pointer + 1, newSource, pointer + 2, entryCnt - 2 - pointer);
				sources = newSource;
			}
		else
			if (pointer < entryCnt - 1) {
				for (int i = 0; i < qs.getSelectedNodeCount(); i++)
					System.arraycopy(data[i], pointer + 1, data[i], pointer + 2, entryCnt - 2 - pointer);
				
				System.arraycopy(sources, pointer + 1, sources, pointer + 1, entryCnt - 2 - pointer);
			}
		pointer ++;
	}

	public void setID(QueryGraphNode n, int insID) {
		data[qs.getNodeID(n)][pointer] = insID;
	}
	
	public void setID(int nodeID, int insID) {
		data[nodeID][pointer] = insID;
	}

	public void beforeFirst() {
		pointer = -1;
	}

	public void close() {
		data = null;
	}

	public int getID(QueryGraphNode n) {
		return data[qs.getNodeID(n)][pointer];
	}
	
	public int getID(int nodeID) {
		return data[nodeID][pointer];
	}

	public boolean next() {
		pointer++;
		
		if (pointer < entryCnt)
			return true;
		else
			return false;
	}

	public void restorePosition() {
		if (savedPosition != -1)
			pointer = savedPosition;
	}

	public void savePosition() {
		savedPosition = pointer;
	}

	public boolean hasNode(QueryGraphNode n) {
		return qs.hasNode(n);
	}

	@Override
	public Set<Integer> getSourceSet() {
		return sources[pointer];
	}

	@Override
	public void setSourceSet(Set<Integer> set) {
		sources[pointer] = set;
	}

}
