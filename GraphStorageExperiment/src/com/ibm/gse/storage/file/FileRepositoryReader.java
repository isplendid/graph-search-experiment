package com.ibm.gse.storage.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.ibm.gse.struct.ConcreteQueryGraphNode;
import com.ibm.gse.system.GraphStorage;

/**
 * The reader that reads entries from a given range
 * @author Tian Yuan
 * 
 */
public class FileRepositoryReader {
	
	RandomAccessFile file;
	byte buf[] = new byte[4096];
	RecordRange range;
	int page = -1, offset = -1;
	int recLen, size;
	
	/**
	 * Create a reader that reads in the given range
	 * @param filename
	 * @param size
	 * @param rr
	 */
	public FileRepositoryReader(String filename, int size, RecordRange range) {
		this.range = range;
		this.size = size;
		recLen = 4 * size;
		try {
			file = new RandomAccessFile(filename, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		beforeFirst();
	}

	private void beforeFirst() {
		try {
			page = range.getStartRID().getPageID();
			offset = range.getStartRID().getOffset() - recLen;
			file.seek((long)page * 4096);
			file.read(buf, 0, 4096);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getID(int nodeID) {
		int numPos = offset + 4 * nodeID;
		try {
			return ((0x00FF & buf[numPos]) << 24) | ((0x00FF & buf[numPos + 1]) << 16) + ((0x00FF & buf[numPos + 2]) << 8) + (0x00FF & buf[numPos + 3]);
		} catch (Exception e) {
			System.out.println(nodeID + " , " + numPos);
			return ((0x00FF & buf[numPos]) << 24) | ((0x00FF & buf[numPos + 1]) << 16) + ((0x00FF & buf[numPos + 2]) << 8) + (0x00FF & buf[numPos + 3]);
		}
	}
	
	private boolean next() {
		offset += recLen;
		if (page > range.getEndRID().getPageID() || page == range.getEndRID().getPageID() && offset > range.getEndRID().getOffset()) return false;
		if (offset + recLen > 4096) {
			page++;
			try {
				file.seek((long)page * 4096);
				file.read(buf, 0, 4096);
			} catch (Exception e) {
				e.printStackTrace();
			}
			offset = 0;
		}
		
		return true;
	}

	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read the next entry from the file
	 * @return The entry read. NULL if no more entry is present.
	 */
	public int[] readEntry() {
		if (next()) {
			int[] result = new int[size];
			
			for (int i = 0; i < size; i++)
				result[i] = getID(i);
			
			return result;
		}
		else
			return null;
	}
}
