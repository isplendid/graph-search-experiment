package sjtu.apex.gse.storage.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

public class SourceHeap {
	final static int pageSize = 4096;
	final static byte bitShift = 12;
	
	private byte buf[] = new byte[pageSize];
	private int offset;
	private RandomAccessFile file;
	
	public SourceHeap(String filename) {
		try {
			file = new RandomAccessFile(filename, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private int readInt(int pos) {
		int value = 0;
		
		if (!(pos >= offset && pos < offset + pageSize)) {
			offset = pos >> bitShift;

			try {
				file.seek(offset);
				file.read(buf, offset, pageSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		value = ((0x00FF & buf[pos]) << 24) | ((0x00FF & buf[pos + 1]) << 16) + ((0x00FF & buf[pos + 2]) << 8) + (0x00FF & buf[pos + 3]);
		
		return value;
	}

	public Set<Integer> getSourceSet(int startIdx, int endIdx) {
		Set<Integer> ret = new HashSet<Integer>();
		int pointer = startIdx;
		
		while (pointer < endIdx) {
			readInt(pointer);
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
