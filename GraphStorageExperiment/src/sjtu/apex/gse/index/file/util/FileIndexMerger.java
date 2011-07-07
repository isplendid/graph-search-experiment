package sjtu.apex.gse.index.file.util;

import sjtu.apex.gse.index.file.FileIndexEntry;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.index.file.FileIndexWriter;
import sjtu.apex.gse.operator.join.Tuple;
import sjtu.apex.gse.storage.file.FileRepositoryEntry;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.FileRepositoryWriter;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapReader;
import sjtu.apex.gse.storage.file.SourceHeapWriter;
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
	public static void merge(String src, String dest, int size, int strSize, int threadCnt) {
		merge(src, dest, "index" + (size - 1), "storage" + (size - 1), "srcheap" + (size - 1), size, strSize, threadCnt);
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
	public static void merge(String src, String dest, String destIdxFn, String destStrFn, String destSrcFn, int size, int strSize, int threadCnt) {
		FileIndexWriter fiw = new FileIndexWriter(dest + "/" + destIdxFn, size, strSize);
		FileRepositoryWriter frw = new FileRepositoryWriter(dest + "/" + destStrFn, size);
		SourceHeapWriter shw = new SourceHeapWriter(dest + "/" + destSrcFn);
		Heap h = new Heap();
		FileIndexReader[] fir = new FileIndexReader[threadCnt];
//		FileRepositoryReader[] frr = new FileRepositoryReader[threadCnt];
		
		for (int i = 0; i < threadCnt; i++) {
			fir[i] = new FileIndexReader(src + "/index" + (size - 1) + ".t" + i, size, strSize);
			
			FileIndexEntry fie;
			
			if ((fie = fir[i].readEntry()) != null) {
				String storageFilename = src + "/storage" + (size - 1) + ".t" + i;
				SourceHeapReader shr = new SourceHeapReader(src + "/srcheap" + (size - 1) + ".t" + i);
				
				h.insert(new HeapContainer(fie, fir[i], storageFilename, shr));
			}
			else
				fir[i].close();
		}
		
		String currentPattern = null;
		HeapContainer hc;
		BindingMerger currentBindings = new BindingMerger(-1);
		
		while ((hc = (HeapContainer)h.remove()) != null) {
			if (currentPattern == null) currentPattern = hc.fie.pattern;
			if (!hc.fie.pattern.equals(currentPattern)) {
				if (!currentPattern.isEmpty()) {
					writePattern(fiw, frw, shw, currentPattern, currentBindings);
					currentBindings = new BindingMerger(-1);
				}

				//Prepare values for the next pattern
				currentPattern = hc.fie.pattern;
			}
			
			//Read all bindings and add to the merger
			FileRepositoryReader frr = new FileRepositoryReader(hc.repFilename, size, hc.fie.range);
			FileRepositoryEntry tmp;
			while ((tmp = frr.readEntry()) != null)
				currentBindings.AddEntry(tmp.bindings, hc.shr.getSourceSet(tmp.sourceHeapRange));
			frr.close();
			
			//Read next index entry and add to the heap 
			hc.fie = hc.reader.readEntry();
			if (hc.fie != null)
				h.insert(hc);
			else {
				hc.reader.close();
				hc.shr.close();
			}
		}
		
		if (!currentBindings.isEmpty())
			writePattern(fiw, frw, shw, currentPattern, currentBindings);
			
		shw.close();
		fiw.close();
		frw.close();
	}
	
	private static void writePattern(FileIndexWriter fiw, FileRepositoryWriter frw, SourceHeapWriter shw, String pattern, BindingMerger bm) {
		RID start, end;
		
		//Writes binding entries
		start = frw.getRID();
		end = start;
		
		for (Tuple t : bm.getTuples()) {
			end = frw.getRID();
			frw.writeEntry(t.getBindings(), shw.writeSet(t.getSources()));
		}
		//Writes an index entry
		fiw.writeEntry(pattern, new RecordRange(start, end), shw.writeSet(bm.getRelevantSources()));
	}
	
	static class HeapContainer implements Comparable<HeapContainer> {
		
		FileIndexEntry fie;
		FileIndexReader reader;
		SourceHeapReader shr;
		String repFilename;
		
		HeapContainer(FileIndexEntry fie, FileIndexReader reader, String repFilename, SourceHeapReader shr) {
			this.fie = fie;
			this.reader = reader;
			this.repFilename = repFilename;
			this.shr = shr;
		}

		@Override
		public int compareTo(HeapContainer arg0) {
			if (arg0 instanceof HeapContainer)
				return -fie.compareTo(((HeapContainer) arg0).fie);
			else
				return 0;
		}
		
	}
	
}
