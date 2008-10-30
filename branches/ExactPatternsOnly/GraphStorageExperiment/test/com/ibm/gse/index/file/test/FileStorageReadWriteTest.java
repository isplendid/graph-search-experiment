package com.ibm.gse.index.file.test;


import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.storage.file.FileRepository;
import com.ibm.gse.storage.file.FileRepositoryWriter;

public class FileStorageReadWriteTest {
	
	final int size = 3;
	FileRepositoryWriter wr;
	FileRepository rd;

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testWriteEntry() {
		wr = new FileRepositoryWriter("data/temp", size);
		int[] data = new int[size];
		for (int i = 0; i < 1000000; i++) {
			for (int j = 0; j < size; j++) data[j] = i * j;
			wr.writeEntry(data);
		}
		wr.close();
	}

}
