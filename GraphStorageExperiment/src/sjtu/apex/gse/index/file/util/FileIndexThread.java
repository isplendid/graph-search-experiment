package sjtu.apex.gse.index.file.util;

import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.index.file.FileIndexWriter;
import sjtu.apex.gse.storage.file.FileRepositoryWriter;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapRange;
import sjtu.apex.gse.storage.file.SourceHeapReader;
import sjtu.apex.gse.storage.file.SourceHeapWriter;
import sjtu.apex.gse.temp.file.TempFileEntry;
import sjtu.apex.gse.temp.file.TempRepositoryFileReader;
import sjtu.apex.gse.temp.file.TempRepositoryFileWriter;
import sjtu.apex.gse.temp.file.util.TempRepositorySorter;

/**
 * This class manages 
 * 
 * @author Tian Yuan
 *
 */
public class FileIndexThread {

	private TempRepositoryFileWriter tw;
	private SourceHeapWriter tsw;
	private String dtfldr, trfn, tsfn, tmpfldr, strgfn, idxfn, shfn, tshfn;
	private int ec, mec, mtc, tc, size, strSize;

	/**
	 * 
	 * @param size
	 * @param strSize
	 * @param maxEntryCount
	 * @param maxThreadCount
	 * @param dataFolder
	 * @param tempFolder
	 */
	public FileIndexThread(int size, int strSize, int maxEntryCount, int maxThreadCount, String dataFolder, String tempFolder) {
		this.size = size;
		this.strSize = strSize;
		dtfldr = dataFolder;
		trfn = tempFolder + "/raw" + (size - 1);
		tsfn = tempFolder + "/sort" + (size - 1);
		tshfn = tempFolder + "/srcheap" + (size - 1);
		tmpfldr = tempFolder + "/SortTmp";
		
		strgfn = dataFolder + "/storage" + (size - 1);
		idxfn = dataFolder + "/index" + (size - 1);
		shfn = dataFolder + "/srcheap" + (size - 1);
		
		tw = new TempRepositoryFileWriter(trfn, size, strSize);
		tsw = new SourceHeapWriter(tshfn); 
		ec = 0;
		tc = 0;
		mec = maxEntryCount;
		mtc = maxThreadCount;
	}

	public void addEntry(String pattern, int[] ins, Set<Integer> src) {
		SourceHeapRange shr = tsw.writeSet(src);
		
		tw.writeRecord(new TempFileEntry(pattern, ins, shr));
		ec++;

		if (ec > mec)
			buildIndex();
	}
	
	public void flush() {
		close();
		tc = 0;
		tw = new TempRepositoryFileWriter(trfn, size, strSize);
	}

	public void close() {
		buildIndex();
		
		tsw.close();
		tw.close();
		FilesystemUtility.deleteFile(trfn);
		FilesystemUtility.deleteFile(tshfn);
		
		mergeIndex();

		if (FilesystemUtility.fileExist(idxfn) && FilesystemUtility.fileExist(strgfn)) {
			FilesystemUtility.renameFile(idxfn, idxfn  + ".t1");
			FilesystemUtility.renameFile(strgfn, strgfn  + ".t1");
			FilesystemUtility.renameFile(shfn, shfn + ".t1");
			FileIndexMerger.merge(dtfldr, dtfldr, size, strSize, 2);
	        for (int i = 0; i < 2; i++) {
	            FilesystemUtility.deleteFile(idxfn + ".t" + i);
	            FilesystemUtility.deleteFile(strgfn + ".t" + i);
	            FilesystemUtility.deleteFile(shfn + ".t" + i);
	        }
		} else {
			FilesystemUtility.renameFile(idxfn + ".t0", idxfn);
			FilesystemUtility.renameFile(strgfn + ".t0", strgfn);
			FilesystemUtility.renameFile(shfn + ".t0", shfn);
		}
	}

	/**
	 * Build index for the temporary files into a thread
	 */
	private void buildIndex() {
		tw.close();
		tsw.close();
		TempRepositorySorter.sort(trfn, tsfn, size, strSize, tmpfldr);
		FilesystemUtility.deleteFile(trfn);
		index(tsfn, tshfn, strgfn + ".t" + tc, idxfn + ".t" + tc, shfn + ".t" + tc, size, strSize);
		FilesystemUtility.deleteFile(tsfn);
		FilesystemUtility.deleteFile(tshfn);
		tc++;

		if (tc > mtc)
			mergeIndex();
		
		tw = new TempRepositoryFileWriter(trfn, size, strSize);
		tsw = new SourceHeapWriter(tshfn);
		ec = 0;
	}

	/**
	 * Merge all threads into a single index
	 */
	private void mergeIndex() {
		if (tc == 1)
			return;
		FileIndexMerger.merge(dtfldr, dtfldr, "index" + (size - 1) + ".t", "storage" + (size - 1) + ".t", "srcheap" + (size - 1) + ".t", size, strSize, tc);
		for (int i = 0; i < tc; i++) {
			FilesystemUtility.deleteFile(idxfn + ".t"  + i);
			FilesystemUtility.deleteFile(strgfn + ".t" + i);
			FilesystemUtility.deleteFile(shfn + ".t" + i);
		}
		FilesystemUtility.renameFile(idxfn + ".t", idxfn + ".t" + "0");
		FilesystemUtility.renameFile(strgfn + ".t", strgfn + ".t" + "0");
		FilesystemUtility.renameFile(shfn + ".t", shfn + ".t" + "0");
		tc = 1;
	}

	/**
	 * This method takes in a sorted temp repository file and generate
	 * a repository file and its index
	 * @param stf
	 * @param dest
	 * @param idx 
	 * @param size
	 */
	private static void index(String stf, String shf, String dest, String idx, String sl, int size, int strSize) {
		TempRepositoryFileReader srd = new TempRepositoryFileReader(stf, size, strSize);
		SourceHeapReader shr = new SourceHeapReader(shf);
		FileRepositoryWriter dwr = new FileRepositoryWriter(dest, size);
		FileIndexWriter iwr = new FileIndexWriter(idx, size, strSize);
		SourceHeapWriter shw = new SourceHeapWriter(sl);
		
		TempFileEntry tfe;
		RID start = null, last = null;
		String currentPat = null;
		Set<Integer> sources = null;

		while ((tfe = srd.readRecord()) != null) {
			if (currentPat == null) {
				start = dwr.getRID();
				currentPat = tfe.pattern;
				sources = new HashSet<Integer>();
			}
			else if (!currentPat.equals(tfe.pattern)) {
				iwr.writeEntry(currentPat, new RecordRange(start, last), shw.writeSet(sources));
				start = dwr.getRID();
				currentPat = tfe.pattern;
				sources = new HashSet<Integer>();
			}

			last = dwr.getRID();
			
			Set<Integer> tmpSrc = shr.getSourceSet(tfe.src);
			
			dwr.writeEntry(tfe.ins, shw.writeSet(tmpSrc));
			sources.addAll(tmpSrc);
		}

		if (currentPat != null) iwr.writeEntry(currentPat, new RecordRange(start, last), shw.writeSet(sources));

		shr.close();
		srd.close();
		dwr.close();
		iwr.close();
		shw.close();
	}
}
