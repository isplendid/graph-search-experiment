package sjtu.apex.gse.temp.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class TempRepositoryFileWriter {
	
	static final int lenSize = 2;
	
	byte buf[] = new byte[4096];
	int recLen, page, offset, size;
	int strSize;
	RandomAccessFile file;
	
	public TempRepositoryFileWriter(String filename, int size, int strSize) {
		try {
			this.strSize = strSize;
			this.size = size;
			file = new RandomAccessFile(filename, "rw");
			recLen = lenSize + strSize + size * 4;
			page = 0;
			offset = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private byte[] intToByteArray (final int i) {
		byte[] dword = new byte[4];
		dword[3] = (byte) (i & 0x00FF);
		dword[2] = (byte) ((i >> 8) & 0x000000FF);
		dword[1] = (byte) ((i >> 16) & 0x000000FF);
		dword[0] = (byte) ((i >> 24) & 0x000000FF);
		return dword;
	}
	
	public void writeRecord(TempFileEntry e) {
		if (offset + recLen >= 4096) {
			try {
				file.setLength(((long)page + 1) * 4096);
				file.write(buf);
				page ++;
				offset = 0;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		byte[] sb = e.pattern.getBytes();
		int len = sb.length;
		
		buf[offset] = (byte)(255 & (len >> 8));
		buf[offset + 1] = (byte)(255 & len);
		
		System.arraycopy(sb, 0, buf, offset + 2, sb.length);
		
		for (int i = 0; i < size; i++) {
			int numPos = offset + lenSize + strSize + i * 4;
			
			System.arraycopy(intToByteArray(e.ins[i]), 0, buf, numPos, 4);
			
//			buf[numPos] = (byte)((byte)(-1) & (e.ins[i] >> 24));
//			buf[numPos + 1] = (byte)((byte)(-1) & (e.ins[i] >> 16));
//			buf[numPos + 2] = (byte)((byte)(-1) & (e.ins[i] >> 8));
//			buf[numPos + 3] = (byte)((byte)(-1) & e.ins[i]);
		}
		
		offset += recLen;
	}
	
	public void close() {
		try {
			file.write(buf, 0, offset);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
