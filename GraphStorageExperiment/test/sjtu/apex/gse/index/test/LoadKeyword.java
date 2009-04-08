package sjtu.apex.gse.index.test;

import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;


public class LoadKeyword {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LabelManager lm = new SleepyCatLabelManager();
		InstanceKeywordRepository ikr = new InstanceKeywordRepository(args[0]);
		lm.load(ikr);
		ikr.close();
	}

}
