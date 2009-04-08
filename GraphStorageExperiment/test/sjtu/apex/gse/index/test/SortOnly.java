package sjtu.apex.gse.index.test;

import sjtu.apex.gse.index.file.util.FileIndexSorter;
import sjtu.apex.gse.indexer.file.FileIndexer;
import sjtu.apex.gse.system.GraphStorage;
import sjtu.apex.gse.temp.file.util.TempRepositorySorter;


public class SortOnly {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		FileIndexSorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/indexTmp" + 2, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 2, 3, GraphStorage.config.getStringSetting("TempFolder", null) + "/sortTmp");
//		TempRepositorySorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 1, GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 1, 2, GraphStorage.config.getStringSetting("TempFolder", null) + "/SortTmp");
//		TempRepositorySorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 0, GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0, 1, "D:/SortTmp");
		FileIndexer.index(GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0, GraphStorage.config.getStringSetting("DataFolder", null) + "/storage" + 0, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 0, 1);
	}

}
