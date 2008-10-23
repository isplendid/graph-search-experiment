package com.ibm.gse.index.test;

import com.ibm.gse.index.file.util.FileIndexMerger;
import com.ibm.gse.system.GraphStorage;

public class IndexMergeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dataFolder = GraphStorage.config.getStringSetting("DataFolder", null);
		FileIndexMerger.merge(dataFolder, dataFolder, 2, 2);
	}

}
