package com.ibm.gse.index.file.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.indexer.file.FileIndexReader;
import com.ibm.gse.system.GraphStorage;

public class FileIndexReaderTest {
	
	final int size = 2;
	FileIndexReader rd;

	@Before
	public void setUp() throws Exception {
		rd = new FileIndexReader(GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + (size - 1), size);
	}

	@Test
	public void testFileIndexReader() throws IOException {
		int counter = 0;
		BufferedWriter wr = new BufferedWriter(new FileWriter("result.txt"));
		
		while (rd.next()) {
			counter ++;
			if (rd.getInstanceCount() > 10)
				wr.append(rd.getPatternString() + "\t" + rd.getInstanceCount() + "\n");
//			if (counter % 1000 == 0) break;
		}
		wr.close();
	}
	
	@After
	public void tearDown() {
		rd.close();
	}

}
