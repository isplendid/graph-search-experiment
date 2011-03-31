package sjtu.apex.gse.index.file;

import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapRange;

public class FileIndexEntry implements Comparable<FileIndexEntry> {
	
	public String pattern;
	public RecordRange range;
	public SourceHeapRange shr;
	
	public FileIndexEntry(String pattern, RecordRange range, SourceHeapRange shr) {
		this.pattern = pattern;
		this.range = range;
		this.shr = shr;
	}

	public int compareTo(FileIndexEntry arg0) {
		if (arg0 instanceof FileIndexEntry)
			return pattern.compareTo(((FileIndexEntry) arg0).pattern);
		else
			return 0;
	}
}
