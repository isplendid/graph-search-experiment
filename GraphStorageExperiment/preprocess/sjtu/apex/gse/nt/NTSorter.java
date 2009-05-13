package sjtu.apex.gse.nt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.util.Heap;

public class NTSorter {
	
	static final int threadMax = 500000;
	
	static public void sort(String src, String dst, String tmpFldr) {
		FilesystemUtility.createDir(tmpFldr);
		
		NTComparator cmp = new NTComparator();
		List<Triple> arr = new ArrayList<Triple>();
		NTReader rd = new NTReader(src);
		NTWriter wr;
		int tc = 0;
		
		while (rd.next()) {
			arr.add(rd.getTriple());
			
			if (arr.size() > threadMax) {
				System.out.println("Generating thread " + tc + "...");
				
				wr = new NTWriter(tmpFldr + "/tmp" + (tc ++));
				
				Collections.sort(arr, cmp);
				
				for (Triple t : arr)
					wr.write(t);
				
				wr.close();
				
				arr = new ArrayList<Triple>();
				
				System.out.println("Done.");
			}
		}
		rd.close();
		
		wr = new NTWriter(tmpFldr + "/tmp" + (tc ++));
		
		Collections.sort(arr, cmp);
		
		for (Triple t : arr)
			wr.write(t);
		
		wr.close();
		
		Heap hp = new Heap();
		wr = new NTWriter(dst);
		
		for (int i = 0; i < tc; i++) {
			rd = new NTReader(tmpFldr + "/tmp" + i);
			
			if (rd.next())
				hp.insert(new NTReaderContainer(rd, cmp));
			else
				rd.close();
		}
		
		while (hp.size() > 0) {
			NTReaderContainer nrc = (NTReaderContainer)hp.remove();
			
			wr.write(nrc.getReader().tri);
			
			if (nrc.getReader().next())
				hp.insert(nrc);
			else
				nrc.getReader().close();
		}
		
		wr.close();
		
		FilesystemUtility.deleteDir(tmpFldr);
	}
	
	static public void main(String[] args) {
		if (args.length != 3)
			System.out.println("Usage : NTSorter source destination tmpFolder");
		else
			NTSorter.sort(args[0], args[1], args[2]);
	}
	
}
