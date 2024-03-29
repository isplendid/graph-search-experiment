package com.ibm.gse.storage.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * The writer that writes entries into repository file
 * @author Tian Yuan
 * 
 */
public class FileRepositoryWriter {
	
	int size;
	RandomAccessFile file;
	byte buf[] = new byte[4096];
	int page = -1, offset = -1;
	int recLen;
	
	public FileRepositoryWriter(String filename, int size) {
		this.size = size;
		recLen = 4 * size;
		try {
			file = new RandomAccessFile(filename, "rw");
			page = 0; offset = 0;
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
	
	public void writeEntry(int[] data) {
		if (offset + recLen > 4096) {
			try {
				file.setLength(((long)page + 1) * 4096);
				file.write(buf);
				page ++;
				offset = 0;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		for (int i = 0; i < size; i++) {
			int numPos = offset + i * 4;
			
			System.arraycopy(intToByteArray(data[i]), 0, buf, numPos, 4);
			
//			buf[numPos] = (byte)(0x00FF & (data[i] >> 24));
//			buf[numPos + 1] = (byte)(255 & (data[i] >> 16));
//			buf[numPos + 2] = (byte)(255 & (data[i] >> 8));
//			buf[numPos + 3] = (byte)(255 & data[i]);
		}
		
		offset += recLen;
	}
	
	public void close() {
		try {
			file.write(buf, 0, offset);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RID getRID() {
		return new RID(page, offset);
	}
}
