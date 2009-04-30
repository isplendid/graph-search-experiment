package sjtu.apex.gse.temp.file;

import java.io.IOException;
import java.io.RandomAccessFile;


public class TempRepositoryFileReader {
	
	static final int lenSize = 2;
	
	byte buf[] = new byte[4096];
	int recLen, page, offset, size;
	RandomAccessFile file;
	int strSize;
	
	public TempRepositoryFileReader(String filename, int size, int strSize) {
		try {
			this.strSize = strSize;
			this.size = size;
			file = new RandomAccessFile(filename, "r");
			recLen = lenSize + strSize + size * 4;
			file.read(buf);
			page = 0;
			offset = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TempFileEntry readRecord() { 
		
		if (offset + recLen >= 4096) {
			try {
				if (((long)page + 1) * 4096 >= file.length()) return null;
				file.read(buf);
				page ++;
				offset = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			if ((long)page * 4096 + offset >= file.length()) return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int res[] = new int[size];
		int len = ((int)(buf[offset]) << 8) + (255 & buf[offset + 1]);
//		System.out.println("OFFSET = " + offset + " LEN = " + len);
		
		String pattern = new String(buf, offset + 2, len);
		
		for (int i = 0; i < size; i++) {
			int numPos = offset + strSize + lenSize + i * 4;
			
			res[i] = ((0x00FF & buf[numPos]) << 24) | ((0x00FF & buf[numPos + 1]) << 16) + ((0x00FF & buf[numPos + 2]) << 8) + (0x00FF & buf[numPos + 3]);
		}
		
		offset += recLen;
		return new TempFileEntry(pattern, res);
	}
	
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
