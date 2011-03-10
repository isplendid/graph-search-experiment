package sjtu.apex.gse.storage.file.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.storage.file.FileRepositoryEntry;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;

public class FileRepositoryReaderTest {
	
	private FileRepositoryReader rd;

	@Before
	public void setUp() throws Exception {
		rd = new FileRepositoryReader("tmp/dat/storage1", 2, new RecordRange(new RID(0, 0), new RID(4, 0)));
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
