package com.ibm.gse.util;

import java.util.ArrayList;
import java.util.List;

import com.ibm.gse.query.Scan;
import com.ibm.gse.query.UpdateScan;
import com.ibm.gse.storage.RAMArrayRepository;
import com.ibm.gse.struct.QuerySchema;

/**
 * The algorithm of merge-sort that sorts the content of a scan
 * @author Tian Yuan
 *
 */
public class MergeSorter {
	
	static final int threadCnt = 3;
	
	private static boolean moveTo(Scan src, UpdateScan dest, QuerySchema sch) {
		dest.insert();
		for (int i = 0; i < sch.getSelectedNodeCount(); i++)
			dest.setID(i, src.getID(i));
		return src.next();
	}
	
	private static int splitToRuns(Scan src, UpdateScan[] dest, QuerySchema sch, EntryComparator comp) {
		int itr = 0;
		int cnt = 1;
		
		while (moveTo(src, dest[itr], sch)) {
			if (comp.compare(src, dest[itr]) < 0) {
				itr = (itr + 1) % dest.length;
				cnt++;
			}
		}
		
		for (int i = 0; i < dest.length; i++) dest[i].beforeFirst();
		
		return cnt;
	}
	
	private static UpdateScan[] initRuns(QuerySchema sch) {
		UpdateScan[] res = new UpdateScan[threadCnt];
		
		for (int i = 0; i < res.length; i++)
			res[i] = new RAMArrayRepository(sch);
		
		return res;
	}
	
	private static int mergeRuns(UpdateScan[] src, UpdateScan dest, QuerySchema sch, EntryComparator comp) {
		List<UpdateScan> unsorted = new ArrayList<UpdateScan>();
		List<UpdateScan> current = new ArrayList<UpdateScan>();
		int fin = 0;
		
		for (int i = 0; i < src.length; i++)
			if (src[i].next()) unsorted.add(src[i]);
		
		while (unsorted.size() > 0) {
			if (current.size() == 0) {
				current.addAll(unsorted); 
				fin++;
			}
			
//			current.get(0).next();
			UpdateScan least = current.get(0);
			for (int i = 1; i < current.size(); i++)
				if (comp.compare(current.get(i), least) < 0)
					least = current.get(i);
			
			if (!moveTo(least, dest, sch)) {
				current.remove(least);
				unsorted.remove(least);
			}
			else if (comp.compare(least, dest) < 0)
				current.remove(least);
		}
				
		for (int i = 0; i < src.length; i++) src[i].close();
		
		return fin;
	}
	
	/**
	 * Sort the content within a scan. Order is given by the comparator.
	 * @param src The source scan
	 * @param sch The schema of the scan
	 * @param comp The comparator which decides the order
	 * @return The scan of ordered results
	 */
	public static Scan sort(Scan src, QuerySchema sch, EntryComparator comp) {
		int crun;
		
		crun = 2;
		
		while (crun > 1) {
			UpdateScan[] runs = initRuns(sch);
			
			src.next();
			crun = splitToRuns(src, runs, sch, comp);
			src = new RAMArrayRepository(sch);
			crun = mergeRuns(runs, (UpdateScan)src, sch, comp);
			src.beforeFirst();
		}		
		
		return src;
	}
}
