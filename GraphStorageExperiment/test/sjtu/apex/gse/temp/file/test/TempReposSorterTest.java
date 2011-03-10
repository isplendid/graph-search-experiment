package sjtu.apex.gse.temp.file.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.SourceHeapRange;
import sjtu.apex.gse.temp.file.TempFileEntry;
import sjtu.apex.gse.temp.file.TempRepositoryFileReader;
import sjtu.apex.gse.temp.file.TempRepositoryFileWriter;
import sjtu.apex.gse.temp.file.util.TempRepositorySorter;


/**
 * 
 * @author Tian Yuan
 * 
 */
public class TempReposSorterTest {
	final static int dataSize = 1000000;
	
	@Test
	public void testWrite() {
		TempRepositoryFileWriter tw = new TempRepositoryFileWriter("data/sort0", 3, 128);
		
		int[] tmp = new int[3];

		for (int i = dataSize - 1; i >= 0; i--) {
			tmp[0] = i;
			tmp[1] = i + 2;
			tmp[2] = i + 3;
			tw.writeRecord(new TempFileEntry(Integer.toString(i), tmp, new SourceHeapRange(new RID(0, 0), new RID(0, 0))));
		}
		tw.close();
	}
	
	@Test
	public void testSort() {
		TempRepositorySorter.sort("data/sort0", "data/sortout0", 3, 128, "data/tmp");
		
		TempRepositoryFileReader rd = new TempRepositoryFileReader("data/sortout0", 3, 128);
		TempFileEntry tfe;
		int cnt = 0;
		
		while ((tfe = rd.readRecord()) != null) {
			assertEquals(Integer.toString(cnt), tfe.pattern);
			assertEquals(cnt, tfe.ins[0]);
			assertEquals(cnt + 2, tfe.ins[1]);
			assertEquals(cnt + 3, tfe.ins[2]);
			cnt++;
		}
		assertEquals(dataSize, cnt);
	}
	
}
