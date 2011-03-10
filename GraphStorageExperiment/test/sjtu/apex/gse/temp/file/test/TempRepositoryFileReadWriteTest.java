package sjtu.apex.gse.temp.file.test;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.SourceHeapRange;
import sjtu.apex.gse.temp.file.TempFileEntry;
import sjtu.apex.gse.temp.file.TempRepositoryFileReader;
import sjtu.apex.gse.temp.file.TempRepositoryFileWriter;

public class TempRepositoryFileReadWriteTest {
	
	private TempRepositoryFileReader rd;
	private TempRepositoryFileWriter wr;

	@Before
	public void setUp() throws Exception {
		wr = new TempRepositoryFileWriter("temptest", 2, 128);
	}

	@After
	public void tearDown() throws Exception {
		int range = 0, base = 0;
		
		for (int i = 0; i < 100000; i++) {
			range += 6 * 4;
			int[] r = new int[2];
			
			r[0] = base;
			r[1] = base + 1;
			wr.writeRecord(new TempFileEntry(Integer.toString(base), r, new SourceHeapRange(new RID(base + 2, base + 3), new RID(base + 4, base + 5))));
			base += 6;
		}
		wr.close();
		
		rd = new TempRepositoryFileReader("temptest", 2, 128);
		
		TempFileEntry tfe;
		base = 0;
		while ((tfe = rd.readRecord()) != null) {
			Assert.assertEquals(base, tfe.ins[0]);
			Assert.assertEquals(base + 1, tfe.ins[1]);
			Assert.assertEquals(Integer.toString(base), tfe.pattern);
			Assert.assertEquals(base + 2, tfe.src.getStartIndex().getPageID());
			Assert.assertEquals(base + 3, tfe.src.getStartIndex().getOffset());
			Assert.assertEquals(base + 4, tfe.src.getEndIndex().getPageID());
			Assert.assertEquals(base + 5, tfe.src.getEndIndex().getOffset());
			
			base += 6;
		}
		
		rd.close();
	}
	
	@Test
	public void test() throws Exception {
		
	}

}
