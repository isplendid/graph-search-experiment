package sjtu.apex.gse.index.file.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.temp.file.TempFileEntry;
import sjtu.apex.gse.temp.file.TempRepositoryFileReader;
import sjtu.apex.gse.temp.file.TempRepositoryFileWriter;


public class TempFileReadWriteTest {
	TempRepositoryFileWriter tw;
	TempRepositoryFileReader tr;
	
	@Before
	public void setUp() {
	}

	@Test
	public void testWriteRecord() {
//		assertNotNull(GraphStorage.config.getStringSetting("TempFileName" + 0, null));
		tw = new TempRepositoryFileWriter("data/test", 3);
		int[] tmp = new int[3];

		for (int i = 0; i < 10000; i++) {
			tmp[0] = i;
			tmp[1] = i + 2;
			tmp[2] = i + 3;
			tw.writeRecord(new TempFileEntry(Integer.toString(i), tmp));
		}
		tw.close();
	}

	@Test
	public void testReadRecord() {
//		fail("Not yet implemented");
		tr = new TempRepositoryFileReader("data/test", 3);
		TempFileEntry tfe;
		int cnt = 0;
		
		while ((tfe = tr.readRecord()) != null) {
			assertEquals(Integer.toString(cnt), tfe.pattern);
			assertEquals(cnt, tfe.ins[0]);
			assertEquals(cnt + 2, tfe.ins[1]);
			assertEquals(cnt + 3, tfe.ins[2]);
			cnt++;
		}
	}

}
