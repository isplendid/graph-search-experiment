package sjtu.apex.gse.storage.file.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.storage.file.FileRepository;
import sjtu.apex.gse.storage.file.SourceHeapReader;

public class FileRepositoryTest {
	
	private static String pattern = "*[+19883::16552]";
	private IndexManager pm;
	private IDManager idman;
		
	@Before
	public void setUp() throws Exception {
		pm = new IndexManager("tmp/dat", 3, 128);
		idman = new SleepyCatIDManager("tmp/id");
	}

	@After
	public void tearDown() throws Exception {
		pm.close();
	}

	@Test
	public void testNext() {
		System.out.println(idman.getURI(19883) + "," + idman.getURI(16552));
		FileRepository fr = new FileRepository(pm, "tmp/dat/storage1", pattern, 2, new SourceHeapReader("tmp/dat/srcheap"));

		while (fr.next()) {
			System.out.println(fr.getID(0) + "," + fr.getID(1));
		}
	}

}
