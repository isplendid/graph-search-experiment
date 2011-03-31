package sjtu.apex.gse.index.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapRange;


/**
 * An index writer that writes entries into an index file 
 * @author Tian Yuan
 * 
 */
public class FileIndexWriter {
	
	static final int lenSize = 2, intSize = 4;
	
	byte[] buf = new byte[4096];
	RandomAccessFile file;
	int recLen;
	int strSize;
	
	public FileIndexWriter(String filename, int size, int strSize) {
		try {
			this.strSize = strSize;
			file = new RandomAccessFile(filename, "rw");
			recLen = strSize + lenSize + intSize * 4 + intSize * 4;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeEntry(FileIndexEntry fie) {
		writeEntry(fie.pattern, fie.range, fie.shr);
	}
	
	public void writeEntry(String pattern, RecordRange r, SourceHeapRange shr) {
		byte[] enc = pattern.getBytes();
		try {
			file.setLength(file.length() + recLen);
			file.writeShort((short)enc.length);
			long lastPos = file.getFilePointer();
			file.write(enc);
			file.seek(lastPos + strSize);
			file.writeInt(r.getStartRID().getPageID());
			file.writeInt(r.getStartRID().getOffset());
			file.writeInt(r.getEndRID().getPageID());
			file.writeInt(r.getEndRID().getOffset());
			file.writeInt(shr.getStartIndex().getPageID());
			file.writeInt(shr.getStartIndex().getOffset());
			file.writeInt(shr.getEndIndex().getPageID());
			file.writeInt(shr.getEndIndex().getOffset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
