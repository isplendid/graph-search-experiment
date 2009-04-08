package sjtu.apex.gse.index.file.util;

import sjtu.apex.gse.index.file.FileIndexEntry;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.index.file.FileIndexWriter;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.FileRepositoryWriter;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.util.Heap;


/**
 * The class that merges indices of the same size into one indices
 * @author Tian Yuan
 * 
 */
public class FileIndexMerger {
	

	/**
	 * This method merges two or more indices into one
	 * @param src The directory containing the indices to be merged
	 * @param dest The directory where the merged index is to be put 
	 * @param size The node count of the indices
	 * @param threadCnt The number of indices to be merged
	 */
	public static void merge(String src, String dest, int size, int threadCnt) {
		merge(src, dest, "index" + (size - 1), "storage" + (size - 1), size, threadCnt);
	}
	
	/**
	 * This method merges two or more indices into one
	 * @param src The directory containing the indices to be merged
	 * @param dest The directory where the merged index is to be put
	 * @param destIdxFn The name of destination index file
	 * @param destStrFn The name of destination storage file 
	 * @param size The node count of the indices
	 * @param threadCnt The number of indices to be merged
	 */
	public static void merge(String src, String dest, String destIdxFn, String destStrFn, int size, int threadCnt) {
		FileIndexWriter fiw = new FileIndexWriter(dest + "/" + destIdxFn, size);
		FileRepositoryWriter frw = new FileRepositoryWriter(dest + "/" + destStrFn, size);
		Heap h = new Heap();
		FileIndexReader[] fir = new FileIndexReader[threadCnt];
//		FileRepositoryReader[] frr = new FileRepositoryReader[threadCnt];
		
		for (int i = 0; i < threadCnt; i++) {
			fir[i] = new FileIndexReader(src + "/index" + (size - 1) + ".t" + i, size);
			
			FileIndexEntry fie;
			
			if ((fie = fir[i].readEntry()) != null)
				h.insert(new HeapContainer(fie, fir[i], src + "/storage" + (size - 1) + ".t" + i));
			else
				fir[i].close();
		}
			
		String currentPattern = null;
		RID start = frw.getRID();
		RID end = null;
		HeapContainer hc;
		
		while ((hc = (HeapContainer)h.remove()) != null) {
			if (currentPattern == null) currentPattern = hc.fie.pattern;
			if (!hc.fie.pattern.equals(currentPattern) && end != null) {
				fiw.writeEntry(currentPattern, new RecordRange(start, end));
				currentPattern = hc.fie.pattern;
				start = frw.getRID();
			}
			
			FileRepositoryReader frr = new FileRepositoryReader(hc.repFilename, size, hc.fie.range);
			int[] tmp;
			while ((tmp = frr.readEntry()) != null) {
				end = frw.getRID();
				frw.writeEntry(tmp);
			}
			frr.close();
						
			hc.fie = hc.reader.readEntry();
			if (hc.fie != null)
				h.insert(hc);
			else
				hc.reader.close();
		}
		fiw.close();
		frw.close();
	}
	
	static class HeapContainer implements Comparable {
		
		FileIndexEntry fie;
		FileIndexReader reader;
		String repFilename;
		
		HeapContainer(FileIndexEntry fie, FileIndexReader reader, String repFilename) {
			this.fie = fie;
			this.reader = reader;
			this.repFilename = repFilename;
		}

		@Override
		public int compareTo(Object arg0) {
			if (arg0 instanceof HeapContainer)
				return -fie.compareTo(((HeapContainer) arg0).fie);
			else
				return 0;
		}
		
	}
	
}
