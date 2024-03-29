package com.ibm.gse.temp.file.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.gse.temp.file.TempFileEntry;
import com.ibm.gse.temp.file.TempRepositoryFileReader;
import com.ibm.gse.temp.file.TempRepositoryFileWriter;
import com.ibm.gse.util.Heap;

/**
 *
 * @author Tian Yuan
 * 
 */
public class TempRepositorySorter {

	final static int segmentLength = 2000000;

	static public void sort(String src, String dest, int size, String tmpDir) {
		sort(src, dest, tmpDir, size, true);
	}

	static public void sort(String src, String dest, String tmpDir, int size, boolean autoDelete) {
		List<TempFileEntry> entryList = new ArrayList<TempFileEntry>();
		TempRepositoryFileReader rd = new TempRepositoryFileReader(src, size);
		TempFileEntry tfe;
		int segCnt = 0;

		if (autoDelete) {
			deleteDir(tmpDir);
			createDir(tmpDir);
		}
		
		while ((tfe = rd.readRecord()) != null) {
			entryList.add(tfe);

			if (entryList.size() >= segmentLength) {
				Collections.sort(entryList, new TempFileEntryComparator());
				writeEntryFile(tmpDir + "/seg" + (segCnt ++), size, entryList);
				entryList.clear();
			}
		}
		
		rd.close();

		Collections.sort(entryList, new TempFileEntryComparator());
		writeEntryFile(tmpDir + "/seg" + (segCnt ++), size, entryList);

		TempRepositoryFileWriter wr = new TempRepositoryFileWriter(dest, size);
		Heap h = new Heap();
		for (int i = 0; i < segCnt; i++) {
			TempRepositoryFileReader trd = new TempRepositoryFileReader(tmpDir + "/seg" + i, size);

			if ((tfe = trd.readRecord()) != null) 
				h.insert(new HeapContainer(trd, tfe));
			else
				trd.close();
		}

		HeapContainer hc;

		while ((hc = (HeapContainer) h.remove()) != null) {
			wr.writeRecord(hc.tfe);
			if ((tfe = hc.rd.readRecord()) != null) {
				hc.updateEntry(tfe);
				h.insert(hc);
			} else
				hc.rd.close();
		}
		wr.close();

		if (autoDelete) {
			deleteDir(tmpDir);
		}
	}

	static private void writeEntryFile(String filename, int size, List<TempFileEntry> ent) {
		TempRepositoryFileWriter wr = new TempRepositoryFileWriter(filename, size);

		for (TempFileEntry e : ent) {
			wr.writeRecord(e);
		}

		wr.close();
	}

	static private void createDir(String dir) {
		File f = new File(dir);
		if (!f.exists()) f.mkdir();
	}

	static private void deleteDir(String dir) {
		deleteDirectory(new File(dir));
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i=0; i<files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return(path.delete());
	}


	static class HeapContainer implements Comparable {
		TempRepositoryFileReader rd;
		TempFileEntry tfe;

		public HeapContainer(TempRepositoryFileReader rd, TempFileEntry tfe) {
			this.tfe = tfe;
			this.rd = rd;
		}

		public void updateEntry(TempFileEntry tfe) {
			this.tfe = tfe;
		}

		@Override
		public int compareTo(Object o) {
			if (o instanceof HeapContainer)
				return -tfe.pattern.compareTo(((HeapContainer)o).tfe.pattern);
			else
				return 0;
		}

	}

}
