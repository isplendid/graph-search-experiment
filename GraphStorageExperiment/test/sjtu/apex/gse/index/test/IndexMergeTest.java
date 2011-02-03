package sjtu.apex.gse.index.test;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.index.file.util.FileIndexMerger;


public class IndexMergeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration config = null;
		String dataFolder = config.getStringSetting("DataFolder", null);
		FileIndexMerger.merge(dataFolder, dataFolder, 2, 128, 2);
	}

}
