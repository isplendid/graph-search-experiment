package com.ibm.gse.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.ibm.gse.storage.file.RID;
import com.ibm.gse.storage.file.RecordRange;
import com.ibm.gse.system.GraphStorage;

/**
 *
 * @author Tian Yuan
 */
public class IndexManager {

	static final int lenSize = 2, intSize = 4;
	static final int strSize = GraphStorage.config.getIntegerSetting("PatternStrSize", 128);
	
	RandomAccessFile file[];
	int recLen;
	
	public IndexManager(int size) {
		file = new RandomAccessFile[size];
		recLen = lenSize + strSize + intSize * 4;
		for (int i = 0; i < size; i++)
			try {
				file[i] = new RandomAccessFile(GraphStorage.config.getStringSetting("DataFolder", "") + "/index" + i, "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	public RecordRange seek(String pattern, int size) {
		RandomAccessFile currentFile = file[size - 1];
		long pos, mid, head, tail;
		short strLen;
		byte buf[] = new byte[strSize];
		
		try {
			head = 0;
			tail = currentFile.length() / recLen - 1;
			
			while (head <= tail) {
				mid = (head + tail) / 2;
				pos = mid * recLen;
				currentFile.seek(pos);
				strLen = currentFile.readShort();
				currentFile.read(buf, 0, strLen);
				String str = new String(buf, 0, strLen);
				
				int cmp = pattern.compareTo(str);
				
				if (cmp < 0)
					tail = mid - 1;
				else if (cmp > 0)
					head = mid + 1;
				else {
					currentFile.seek(pos + lenSize + strSize);
					int spageID = currentFile.readInt();
					int soffset = currentFile.readInt();
					int epageID = currentFile.readInt();
					int eoffset = currentFile.readInt();
					
					return new RecordRange(new RID(spageID, soffset), new RID(epageID, eoffset));
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getPatternCount(String pattern, int size) {
		if (size > file.length) return -1;
		RecordRange rr = seek(pattern, size);
		if (rr == null)
			return -1;
		long startpos = ((long)rr.getStartRID().getPageID() * 4096) + rr.getStartRID().getOffset();
		long endpos = ((long)rr.getEndRID().getPageID() * 4096) + rr.getEndRID().getOffset();
		
		return (int)((endpos - startpos)/(4 * size)) + 1;
	}
}
