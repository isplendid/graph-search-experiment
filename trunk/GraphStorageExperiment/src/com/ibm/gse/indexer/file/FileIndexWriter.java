package com.ibm.gse.indexer.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.ibm.gse.storage.file.RecordRange;
import com.ibm.gse.system.GraphStorage;

/**
 * An index writer that writes entries into an index file 
 * @author Tian Yuan
 * 
 */
public class FileIndexWriter {
	
	static final int lenSize = 2, intSize = 4;
	static final int strSize = GraphStorage.config.getIntegerSetting("PatternStrSize", 128);
	
	byte[] buf = new byte[4096];
	RandomAccessFile file;
	int recLen;
	
	public FileIndexWriter(String filename, int size) {
		try {
			file = new RandomAccessFile(filename, "rw");
			recLen = strSize + lenSize + intSize * 4;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeEntry(String pattern, RecordRange r) {
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
