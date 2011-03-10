package sjtu.apex.gse.storage.file.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.storage.file.FileRepositoryEntry;
import sjtu.apex.gse.storage.file.FileRepositoryReader;


public class FileRepositoryReaderPatternTest {
	private FileRepositoryReader rd;

	@Before
	public void setUp() throws Exception {
		rd = new FileRepositoryReader("tmp/dat/storage1", 2, new IndexManager("tmp/dat", 3, 128).seek("*[-16555::16552]", 2));
	}
	
	@Test
	public void test() throws Exception {
		FileRepositoryEntry fre;
		while ((fre = rd.readEntry()) != null) {
			System.out.println(fre.bindings[0] + " " + fre.bindings[1]);
		}
		rd.close();
	}

	@After
	public void tearDown() throws Exception {
	}

}
