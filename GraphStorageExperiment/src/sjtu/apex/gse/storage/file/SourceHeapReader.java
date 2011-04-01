package sjtu.apex.gse.storage.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

public class SourceHeapReader {
	final static int pageSize = 4096;
	final static byte bitShift = 12;
	
	private byte buf[] = new byte[pageSize];
	private long pageStart;
	private RandomAccessFile file;
	
	public SourceHeapReader(String filename) {
		try {
			file = new RandomAccessFile(filename, "r");
			pageStart = -Integer.MAX_VALUE;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private int readInt(long pos) {
		int value = 0;
		int offset;
		
		if (!(pos >= pageStart && pos < pageStart + pageSize)) {
			pageStart = pos & 0xfffff000;
			try {
				file.seek(pageStart);
				file.read(buf, 0, pageSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		offset = (int)(pos - pageStart);
		
		value = ((0x00FF & buf[offset]) << 24) | ((0x00FF & buf[offset + 1]) << 16) + ((0x00FF & buf[offset + 2]) << 8) + (0x00FF & buf[offset + 3]);
		
		return value;
	}
	
	public Set<Integer> getSourceSet(SourceHeapRange range) {
		return getSourceSet(range.getStartIndex().getPageID(), range.getStartIndex().getOffset(), range.getEndIndex().getPageID(), range.getEndIndex().getOffset());
	}
	
	public Set<Integer> getSourceSet(int startPage, int startOffset, int endPage, int endOffset) {
		Set<Integer> ret = new HashSet<Integer>();
		long startIdx = (startPage << bitShift | startOffset);
		long endIdx = (endPage << bitShift | endOffset);
		long pointer = startIdx;
		
		while (pointer < endIdx) {
			ret.add(readInt(pointer));
			pointer += 4;
		}
		
		return ret;
	}
	
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
