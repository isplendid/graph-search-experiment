package sjtu.apex.gse.operator.test;

import sjtu.apex.gse.indexer.file.SleepyCatIDManager;

public class IDManagerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SleepyCatIDManager idm = null;//new SleepyCatIDManager(null);
		
		System.out.println(idm.getURI(1764));
		
		System.out.println(idm.getURI(4159));
		
		System.out.println(idm.getURI(1762));
	}

}
