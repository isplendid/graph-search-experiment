package sjtu.apex.gse.search.test;

import sjtu.apex.gse.indexer.file.SleepyCatIDManager;

public class IDManagerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SleepyCatIDManager idm = new SleepyCatIDManager();
		
		System.out.println(idm.getURI(1764));
		
		System.out.println(idm.getURI(4159));
		
		System.out.println(idm.getURI(1762));
	}

}
