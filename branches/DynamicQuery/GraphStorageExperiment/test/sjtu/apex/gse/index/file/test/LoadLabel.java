package sjtu.apex.gse.index.file.test;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;

public class LoadLabel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LabelManager labman = new SleepyCatLabelManager(new FileConfig(args[0]));
		
		labman.load(new InstanceKeywordRepository(args[1]));
		labman.close();

	}

}
