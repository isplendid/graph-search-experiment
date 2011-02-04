package sjtu.apex.gse.search.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.storage.file.FileRepository;


public class IndexSearchTest {
	
	IDManager idman;
	List<String> patterns;
	List<Integer> size;

	@Before
	public void setUp() throws Exception {
		idman = null;//new SleepyCatIDManager(null);
		patterns = new ArrayList<String>();
		size = new ArrayList<Integer>();
		
//		/* TESTCASE 1 */
//		patterns.add("ship");
//		size.add(1);
//		
//		/* TESTCASE 2 */
//		patterns.add("risk[+related::high]");
//		size.add(2);
//		
//		/* TESTCASE 3 */
//		patterns.add("account");
//		size.add(1);
//		
//		/* TESTCASE 4 */
//		patterns.add("accepted[+related::data]");
//		size.add(2);
		
//		/* TESTCASE 5 */
//		patterns.add("need[+contain::polici]");
//		size.add(2);
//		
//		/* TESTCASE 6 */
//		patterns.add("need[+contain::type]");
//		size.add(2);
		
		/* TESTCASE 7 */
//		patterns.add("*[+22-rdf-syntax-ns#type::*]");
//		size.add(2);
		
		patterns.add("GraduateStudent0");
		size.add(1);
	}
	
//	@Test
//	public void testSearchIndex() {
//		FileIndex fi = new FileIndex(2);
//		
//		for (int i = 0; i < patterns.size(); i++) {
//			RecordRange rr = fi.seek(patterns.get(i), size.get(i));
//			
//			System.out.println("FROM " + rr.getStartRID() + " TO " + rr.getEndRID());
//		}
//	}

	@Test
	public void testFileRepository() {
		FileRepository fr;
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < patterns.size(); i++) {
			fr = new FileRepository(null, null, patterns.get(i), size.get(i), null);
			
			System.out.println(patterns.get(i));
			
			int count = 0;
			while (fr.next()) {
				count ++;
				if (count < 10 || fr.getID(1) == 4159) {
					for (int j = 0; j < size.get(i); j++)
//					System.out.print(idman.getURI(fr.getID(j)) + " ");
						System.out.print(fr.getID(j) + " ");
					System.out.println();
				}
			}
			System.out.println("Count = " + count);
		}
		System.out.println(System.currentTimeMillis() - time);
	}

}
