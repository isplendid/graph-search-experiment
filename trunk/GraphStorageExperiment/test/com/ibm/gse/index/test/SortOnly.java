package com.ibm.gse.index.test;

import com.ibm.gse.index.file.util.FileIndexSorter;
import com.ibm.gse.indexer.file.FileIndexer;
import com.ibm.gse.system.GraphStorage;
import com.ibm.gse.temp.file.util.TempRepositorySorter;

public class SortOnly {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		FileIndexSorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/indexTmp" + 2, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 2, 3, GraphStorage.config.getStringSetting("TempFolder", null) + "/sortTmp");
//		TempRepositorySorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 1, GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 1, 2, GraphStorage.config.getStringSetting("TempFolder", null) + "/SortTmp");
//		FileIndexer.index(GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 1, GraphStorage.config.getStringSetting("DataFolder", null) + "/storage" + 1, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 1, 2);
	}

}
