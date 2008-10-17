package com.ibm.gse.search.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.indexer.db.IDManager;
import com.ibm.gse.storage.file.FileRepository;

public class IndexSearchTest {
	
	IDManager idman;
	List<String> patterns;
	List<Integer> size;

	@Before
	public void setUp() throws Exception {
		idman = new IDManager();
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
		patterns.add("metamodel[+hasMember::polici,+hasMember::xmln]");
		size.add(3);
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
			fr = new FileRepository(patterns.get(i), size.get(i));
			
			System.out.println(patterns.get(i));
			while (fr.next()) {
				for (int j = 0; j < size.get(i); j++)
//					System.out.print(idman.getURI(fr.getID(j)) + " ");
					System.out.print(fr.getID(j) + " ");
				System.out.println();
			}
		}
		
		System.out.println(System.currentTimeMillis() - time);
	}

}
