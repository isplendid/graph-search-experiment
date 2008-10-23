package com.ibm.gse.index.test;

import com.ibm.gse.indexer.InstanceKeywordRepository;
import com.ibm.gse.indexer.LabelManager;
import com.ibm.gse.indexer.file.SleepyCatLabelManager;

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
