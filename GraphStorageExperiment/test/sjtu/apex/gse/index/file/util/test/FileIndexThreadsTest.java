package sjtu.apex.gse.index.file.util.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.index.file.util.FileIndexThread;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.storage.file.FileRepositoryEntry;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.RecordRange;
import sun.security.util.Debug;


public class FileIndexThreadsTest {
	
	private FileIndexThread fit;
	private final static int maxIns = 100000000;
	private List<List<Set<Integer>>> lls;
	
	@Before
	public void setUp() {
		fit = new FileIndexThread(2, 128, 100, 2, "dbg/dat", "dbg/tmp");
		lls = new ArrayList(4);
		
		for (int i = 0; i < 4; i++) {
			lls.add(new ArrayList(2));
			for (int j = 0; j < 2; j++)
				lls.get(i).add(new HashSet<Integer>());
		}
		
		for (int i = 0; i < maxIns; i++) {
			int base = i * 2;
			int[] r = new int[2];
			
			r[0] = base;
			r[1] = base + 1;
			
			lls.get(i % 4).get(0).add(base);
			lls.get(i % 4).get(1).add(base + 1);
			
			Set<Integer> srclist = new HashSet<Integer>();
			
			for (int j = 0; j <= i % 4; j++)
				srclist.add(j);
			
			fit.addEntry(Integer.toString(i % 4), r, srclist);
		}
		fit.close();
	}
	
	@Test
	public void test() {
		IndexManager im = new IndexManager("dbg/dat", 2, 128);
		
		for (int i = 0; i < 4; i++) {
			RecordRange rr = im.seek(Integer.toString(i), 2);
			FileRepositoryReader frr = new FileRepositoryReader("dbg/dat/storage1", 2, rr);
			FileRepositoryEntry fre;
			
			Debug.println("Debug", "Pattern " + i);
			
			while ((fre = frr.readEntry()) != null) {
				int s = fre.bindings[0];
				int o = fre.bindings[1];
				
				Debug.println("Debug", Integer.toString(s) + ", " + Integer.toString(o));
				Assert.assertTrue(lls.get(i).get(0).contains(s));
				Assert.assertTrue(lls.get(i).get(1).contains(o));
				
				
				lls.get(i).get(0).remove(s);
				lls.get(i).get(1).remove(o);
			}
			frr.close();
			
			Assert.assertTrue(lls.get(i).get(0).size() == 0);
			Assert.assertTrue(lls.get(i).get(1).size() == 0);
		}
	}
	
	
}
