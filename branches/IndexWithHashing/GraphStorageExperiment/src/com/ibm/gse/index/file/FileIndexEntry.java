package com.ibm.gse.index.file;

import com.ibm.gse.storage.file.RecordRange;

public class FileIndexEntry implements Comparable{
	
	public String pattern;
	public RecordRange range;
	
	public FileIndexEntry(String pattern, RecordRange range) {
		this.pattern = pattern;
		this.range = range;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof FileIndexEntry)
			return pattern.compareTo(((FileIndexEntry) arg0).pattern);
		else
			return 0;
	}
}
