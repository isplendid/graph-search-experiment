package sjtu.apex.gse.index.file.util;

import java.util.Set;

import sjtu.apex.gse.config.Configuration;



/**
 *
 * @author Tian Yuan
 * 
 */
public class FileIndexer {
	final static int maxEntryCnt = 10000000, maxThreadCnt = 3;
	
	private FileIndexThreads[] threads;
	private String dtfldr, tmpfldr;
	
	public FileIndexer(int maxSize, Configuration config) {
		int strSize = config.getIntegerSetting("PatternStrSize", 128);
		threads = new FileIndexThreads[maxSize];
		dtfldr = config.getStringSetting("DataFolder", null);
		tmpfldr = config.getStringSetting("TempFolder", null);
		
		for (int i = 0; i < maxSize; i++)
			threads[i] = new FileIndexThreads(i + 1, strSize, maxEntryCnt, maxThreadCnt, dtfldr, tmpfldr);
	}
	
	/**
	 * 
	 * @param pattern
	 * @param size
	 * @param ins
	 */
	public void addEntry(String pattern, int size, int[] ins, Set<Integer> src) {
		threads[size - 1].addEntry(pattern, ins, src);
	}
	
	/**
	 * 
	 */
	public void close() {
		for (FileIndexThreads t : threads)
			t.close();
	}
	
	public void flushAll() {
		for (FileIndexThreads t : threads)
			t.flush();
	}
	
	public void flush(int size) {
		threads[size - 1].flush();
	}
	
}
