package sjtu.apex.gse.index.file.util;

import sjtu.apex.gse.system.GraphStorage;



/**
 *
 * @author Tian Yuan
 * 
 */
public class FileIndexer {
	final static int maxEntryCnt = 10000000, maxThreadCnt = 3;
	
	private FileIndexThreads[] threads;
	private String dtfldr, tmpfldr;
	
	public FileIndexer(int maxSize) {
		threads = new FileIndexThreads[maxSize];
		dtfldr = GraphStorage.config.getStringSetting("DataFolder", null);
		tmpfldr = GraphStorage.config.getStringSetting("TempFolder", null);
		
		for (int i = 0; i < maxSize; i++)
			threads[i] = new FileIndexThreads(i + 1, maxEntryCnt, maxThreadCnt, dtfldr, tmpfldr);
	}
	
	/**
	 * 
	 * @param pattern
	 * @param size
	 * @param ins
	 */
	public void addEntry(String pattern, int size, int[] ins) {
		threads[size - 1].addEntry(pattern, ins);
	}
	
	/**
	 * 
	 */
	public void close() {
		for (FileIndexThreads t : threads)
			t.close();
	}
	
}
