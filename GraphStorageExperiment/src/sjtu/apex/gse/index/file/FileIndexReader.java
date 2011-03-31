package sjtu.apex.gse.index.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapRange;


public class FileIndexReader {
	
	static final int lenSize = 2, intSize = 4;
	static final int bitShift = 12;
	
	RandomAccessFile file;
	int recLen;
	int entLen;
	long pointer;
	int strSize;

	public FileIndexReader(String filename, int size, int strSize) {
		try {
			file = new RandomAccessFile(filename, "r");
			recLen = strSize + lenSize + intSize * 8;
			entLen = intSize * size + intSize * 8;
			pointer = -recLen;
			this.strSize = strSize;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void beforeFirst() {
		pointer = -recLen;
	}
	
	public boolean next() {
		pointer += recLen; 
		try {
			return (pointer < file.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getPatternString() {
		short len = 0;
		
		try {
			file.seek(pointer);
			len = file.readShort();
			byte[] charBuf = new byte[strSize];
			file.read(charBuf, 0, len);
			
			return new String(charBuf, 0, len);
		} catch (Exception e) {
			System.err.println("POSITION = " + pointer + " , LEN = " + Short.toString(len));
			e.printStackTrace();
		}
		
		return null;
	}
	
	public RecordRange getRange() {
		try {
			file.seek(pointer + strSize + lenSize);
			int sp = file.readInt();
			int so = file.readInt();
			int ep = file.readInt();
			int eo = file.readInt();
			
			return new RecordRange(new RID(sp, so), new RID(ep, eo));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public SourceHeapRange getSourceHeapRange() {
		try {
			file.seek(pointer + strSize + lenSize + 16);
			int sp = file.readInt();
			int so = file.readInt();
			int ep = file.readInt();
			int eo = file.readInt();
			
			return new SourceHeapRange(new RID(sp, so), new RID(ep, eo));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public FileIndexEntry readEntry() {
		if (next())
			return new FileIndexEntry(getPatternString(), getRange(), getSourceHeapRange());
		else
			return null;
	}
	
	public int getInstanceCount() {
		RecordRange rr = getRange();
		
		long startpos = (long)(rr.getStartRID().getPageID()) << bitShift + (long)(rr.getStartRID().getOffset());
		long endpos = (long)(rr.getEndRID().getPageID()) << bitShift + (long)(rr.getEndRID().getOffset());
		
		return (int) ((endpos - startpos) / entLen + 1);
	}
	
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
