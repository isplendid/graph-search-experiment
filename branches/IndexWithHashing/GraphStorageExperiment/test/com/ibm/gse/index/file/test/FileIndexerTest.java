package com.ibm.gse.index.file.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.gse.indexer.file.FileIndexer;

public class FileIndexerTest {

	@Test
	public void testIndex() {
		FileIndexer.index("data/sortout0", "data/storage0", "data/idx0", 3);
	}

}
