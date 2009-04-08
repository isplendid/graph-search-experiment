package sjtu.apex.gse.index.test;

import sjtu.apex.gse.index.file.util.FileIndexMerger;
import sjtu.apex.gse.system.GraphStorage;


public class IndexMergeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dataFolder = GraphStorage.config.getStringSetting("DataFolder", null);
		FileIndexMerger.merge(dataFolder, dataFolder, 2, 2);
	}

}
