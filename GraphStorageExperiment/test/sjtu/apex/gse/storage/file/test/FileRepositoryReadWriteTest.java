package sjtu.apex.gse.storage.file.test;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.storage.file.FileRepositoryEntry;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.FileRepositoryWriter;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapRange;

public class FileRepositoryReadWriteTest {
	
	private FileRepositoryWriter wr;
	private FileRepositoryReader rd;

	@Before
	public void setUp() throws Exception {
		wr = new FileRepositoryWriter("teststorage", 2);
	}
	
	@Test
	public void test() throws Exception {
		int range = 0;
		
		for (int i = 0; i < 100000; i++) {
			range += 6 * 4;
			int base = i * 6;
			int[] r = new int[2];
			
			r[0] = base;
			r[1] = base + 1;
			wr.writeEntry(new FileRepositoryEntry(r, new SourceHeapRange(new RID(base + 2, base + 3), new RID(base + 4, base + 5))));
		}
		wr.close();
		
		rd = new FileRepositoryReader("teststorage", 2, new RecordRange(new RID(0, 0), new RID(range / 4096, range % 4096)));
		FileRepositoryEntry fre;
		int base = 0;
		while ((fre = rd.readEntry()) != null) {
			Assert.assertEquals(fre.bindings[0], base);
			Assert.assertEquals(fre.bindings[1], base + 1);
			Assert.assertEquals(fre.sourceHeapRange.getStartIndex().getPageID(), base + 2);
			Assert.assertEquals(fre.sourceHeapRange.getStartIndex().getOffset(), base + 3);
			Assert.assertEquals(fre.sourceHeapRange.getEndIndex().getPageID(), base + 4);
			Assert.assertEquals(fre.sourceHeapRange.getEndIndex().getOffset(), base + 5);
			base += 6;
		}
		rd.close();
	}

	@After
	public void tearDown() throws Exception {
	}

}
