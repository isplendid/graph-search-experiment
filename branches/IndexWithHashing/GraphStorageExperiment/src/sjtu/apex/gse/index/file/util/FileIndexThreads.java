package sjtu.apex.gse.index.file.util;

import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.index.file.FileIndexWriter;
import sjtu.apex.gse.storage.file.FileRepositoryWriter;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
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
public class FileIndexThreads {

	private TempRepositoryFileWriter tw;
	private String dtfldr, trfn, tsfn, tmpfldr, strgfn, idxfn;
	private int ec, mec, mtc, tc, size;

	public FileIndexThreads(int size, int maxEntryCount, int maxThreadCount, String dataFolder, String tempFolder) {
		this.size = size;
		dtfldr = dataFolder;
		trfn = tempFolder + "/raw" + (size - 1);
		tsfn = tempFolder + "/sort" + (size - 1);
		tmpfldr = tempFolder + "/SortTmp";
		strgfn = dataFolder + "/storage" + (size - 1);
		idxfn = dataFolder + "/index" + (size - 1);
		tw = new TempRepositoryFileWriter(trfn, size);
		ec = 0;
		tc = 0;
		mec = maxEntryCount;
		mtc = maxThreadCount;
	}

	public void addEntry(String pattern, int[] ins) {
		tw.writeRecord(new TempFileEntry(pattern, ins));
		ec++;

		if (ec > mec) buildIndex();
	}

	public void close() {
		buildIndex();
		
		tw.close();
		FilesystemUtility.deleteFile(trfn);
		
		mergeIndex();

		if (FilesystemUtility.fileExist(idxfn) && FilesystemUtility.fileExist(strgfn)) {
			FilesystemUtility.renameFile(idxfn, idxfn  + ".t1");
			FilesystemUtility.renameFile(strgfn, strgfn  + ".t1");       
			FileIndexMerger.merge(dtfldr, dtfldr, "index" + (size - 1), "storage" + (size - 1), size, 2);
	        for (int i = 0; i < 2; i++) {
	            FilesystemUtility.deleteFile(idxfn + ".t" + i);
	            FilesystemUtility.deleteFile(strgfn + ".t" + i);
	        }
		} else {
			FilesystemUtility.renameFile(idxfn + ".t0", idxfn);
			FilesystemUtility.renameFile(strgfn + ".t0", strgfn);
		}
	}

	/**
	 * Build index for the temporary files into a thread
	 */
	private void buildIndex() {
		tw.close();
		TempRepositorySorter.sort(trfn, tsfn, size, tmpfldr);
		FilesystemUtility.deleteFile(trfn);
		index(tsfn, strgfn + ".t" + tc, idxfn + ".t" + tc, size);
		FilesystemUtility.deleteFile(tsfn);
		tc++;

		if (tc > mtc)
			mergeIndex();
		
		tw = new TempRepositoryFileWriter(trfn, size);
		ec = 0;
	}

	/**
	 * Merge all threads into a single index
	 */
	private void mergeIndex() {
		if (tc == 1)
			return;
		FileIndexMerger.merge(dtfldr, dtfldr, "index" + (size - 1) + ".t", "storage" + (size - 1) + ".t", size, tc);
		for (int i = 0; i < tc; i++) {
			FilesystemUtility.deleteFile(idxfn + ".t"  + i);
			FilesystemUtility.deleteFile(strgfn + ".t" + i);
		}
		FilesystemUtility.renameFile(idxfn + ".t", idxfn + ".t" + "0");
		FilesystemUtility.renameFile(strgfn + ".t", strgfn + ".t" + "0");
		tc = 1;
	}

	/**
	 * This method takes in a sorted temp repository file and generate
	 * a repository file and its index
	 * @param src
	 * @param dest
	 * @param idx 
	 * @param size
	 */
	static private void index(String src, String dest, String idx, int size) {
		TempRepositoryFileReader srd = new TempRepositoryFileReader(src, size);
		FileRepositoryWriter dwr = new FileRepositoryWriter(dest, size);
		FileIndexWriter iwr = new FileIndexWriter(idx, size);
		TempFileEntry tfe;
		RID start = null, last = null;
		String currentPat = null;

		while ((tfe = srd.readRecord()) != null) {
			if (currentPat == null) {
				start = dwr.getRID();
				currentPat = tfe.pattern;
			}
			else if (!currentPat.equals(tfe.pattern)) {
				iwr.writeEntry(currentPat, new RecordRange(start, last));
				start = dwr.getRID();
				currentPat = tfe.pattern;
			}

			last = dwr.getRID();
			dwr.writeEntry(tfe.ins);
		}

		if (currentPat != null) iwr.writeEntry(currentPat, new RecordRange(start, last));

		srd.close();
		dwr.close();
		iwr.close();
	}
}
