package sjtu.apex.gse.storage.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

public class SourceHeapWriter {
	final static int pageSize = 4096;
	final static byte bitShift = 12;
	
	private byte buf[] = new byte[pageSize];
	private long pageStart;
	private long pointer;
	private long fileSize;
	private RandomAccessFile file;
	
	public SourceHeapWriter(String filename) {
		try {
			file = new RandomAccessFile(filename, "rw");
			try {
				fileSize = file.getChannel().size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pageStart = fileSize & ~(pageSize - 1);
		pointer = fileSize;
	}
	
	public void close() {
		try {
			if (pointer > fileSize) {
				file.setLength(pointer);
				file.seek(pageStart);
				file.write(buf, 0 , (int)pointer % pageSize);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeInt(int value) {
		if (pointer < pageStart || pointer >= pageStart + pageSize) {
			try {
				if (fileSize < pageStart + pageSize) {
					fileSize += pageSize;
					file.setLength(fileSize);
				}
				file.seek(pageStart);
				file.write(buf, 0, pageSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			pageStart = pointer & ~(pageSize - 1);
		}
		int offset = (int)(pointer & (pageSize - 1));
		buf[offset + 3] = (byte) (value & 0x00FF);
		buf[offset + 2] = (byte) ((value >> 8) & 0x000000FF);
		buf[offset + 1] = (byte) ((value >> 16) & 0x000000FF);
		buf[offset] = (byte) ((value >> 24) & 0x000000FF);
		pointer += 4;
	}
	
	public SourceHeapRange writeSet(Set<Integer> value) {
		long startIdx, endIdx;
		
		startIdx = pointer;
		for (int i : value) writeInt(i);
		endIdx = pointer;
		
		return new SourceHeapRange(new RID((int)(startIdx >> bitShift), (int)(startIdx & (pageSize - 1))), new RID((int)(endIdx >> bitShift), (int)(endIdx & (pageSize - 1))));
	}
	 
}
