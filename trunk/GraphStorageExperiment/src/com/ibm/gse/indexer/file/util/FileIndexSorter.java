package com.ibm.gse.indexer.file.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.gse.indexer.file.FileIndexEntry;
import com.ibm.gse.indexer.file.FileIndexReader;
import com.ibm.gse.indexer.file.FileIndexWriter;
import com.ibm.gse.util.Heap;

/**
 *
 * @author Tian Yuan
 * 
 */
public class FileIndexSorter {

	final static int segmentLength = 500000;

	static public void sort(String src, String dest, int size, String tmpDir) {
		sort(src, dest, tmpDir, size, true);
	}

	static public void sort(String src, String dest, String tmpDir, int size, boolean autoDelete) {
		List<FileIndexEntry> entryList = new ArrayList<FileIndexEntry>();
		FileIndexReader rd = new FileIndexReader(src, size);
		FileIndexEntry tfe;
		int segCnt = 0;

		if (autoDelete) {
			deleteDir(tmpDir);
			createDir(tmpDir);
		}
		
		while (rd.next()) {
			tfe = rd.readEntry();
			entryList.add(tfe);

			if (entryList.size() >= segmentLength) {
				Collections.sort(entryList);
				writeEntryFile(tmpDir + "/seg" + (segCnt ++), size, entryList);
				entryList.clear();
			}
		}
		
		rd.close();

		Collections.sort(entryList);
		writeEntryFile(tmpDir + "/seg" + (segCnt ++), size, entryList);

		FileIndexWriter wr = new FileIndexWriter(dest, size);
		Heap h = new Heap();
		for (int i = 0; i < segCnt; i++) {
			FileIndexReader trd = new FileIndexReader(tmpDir + "/seg" + i, size);

			if (trd.next()) {
				tfe = trd.readEntry();
				h.insert(new HeapContainer(trd, tfe));
			}
			else
				trd.close();
		}

		HeapContainer hc;

		while ((hc = (HeapContainer) h.remove()) != null) {
			wr.writeEntry(hc.tfe.pattern, hc.tfe.range);
			if (hc.rd.next()) {
				tfe = hc.rd.readEntry();
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

	static private void writeEntryFile(String filename, int size, List<FileIndexEntry> ent) {
		FileIndexWriter wr = new FileIndexWriter(filename, size);

		for (FileIndexEntry e : ent) {
			wr.writeEntry(e.pattern, e.range);
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
		FileIndexReader rd;
		FileIndexEntry tfe;

		public HeapContainer(FileIndexReader rd, FileIndexEntry tfe) {
			this.tfe = tfe;
			this.rd = rd;
		}

		public void updateEntry(FileIndexEntry tfe) {
			this.tfe = tfe;
		}

		@Override
		public int compareTo(Object o) {
			if (o instanceof HeapContainer)
				return -tfe.compareTo(((HeapContainer) o).tfe);
			else
				return 0;
		}

	}

}
