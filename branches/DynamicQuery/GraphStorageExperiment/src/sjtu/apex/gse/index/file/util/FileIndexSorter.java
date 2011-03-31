package sjtu.apex.gse.index.file.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.index.file.FileIndexEntry;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.index.file.FileIndexWriter;
import sjtu.apex.gse.util.Heap;


/**
 *
 * @author Tian Yuan
 * 
 */
public class FileIndexSorter {

	final static int segmentLength = 500000;

	static public void sort(String src, String dest, int size, int strSize, String tmpDir) {
		sort(src, dest, tmpDir, size, strSize, true);
	}

	static public void sort(String src, String dest, String tmpDir, int size, int strSize, boolean autoDelete) {
		List<FileIndexEntry> entryList = new ArrayList<FileIndexEntry>();
		FileIndexReader rd = new FileIndexReader(src, size, strSize);
		FileIndexEntry tfe;
		int segCnt = 0;

		if (autoDelete) {
			FilesystemUtility.deleteDir(tmpDir);
			FilesystemUtility.createDir(tmpDir);
		}
		
		while (rd.next()) {
			tfe = rd.readEntry();
			entryList.add(tfe);

			if (entryList.size() >= segmentLength) {
				Collections.sort(entryList);
				writeEntryFile(tmpDir + "/seg" + (segCnt ++), size, strSize, entryList);
				entryList.clear();
			}
		}
		
		rd.close();

		Collections.sort(entryList);
		writeEntryFile(tmpDir + "/seg" + (segCnt ++), size, strSize, entryList);

		FileIndexWriter wr = new FileIndexWriter(dest, size, strSize);
		Heap h = new Heap();
		for (int i = 0; i < segCnt; i++) {
			FileIndexReader trd = new FileIndexReader(tmpDir + "/seg" + i, size, strSize);

			if (trd.next()) {
				tfe = trd.readEntry();
				h.insert(new HeapContainer(trd, tfe));
			}
			else
				trd.close();
		}

		HeapContainer hc;

		while ((hc = (HeapContainer) h.remove()) != null) {
			wr.writeEntry(hc.tfe.pattern, hc.tfe.range, hc.tfe.shr);
			if (hc.rd.next()) {
				tfe = hc.rd.readEntry();
				hc.updateEntry(tfe);
				h.insert(hc);
			} else
				hc.rd.close();
		}
		wr.close();

		if (autoDelete) {
			FilesystemUtility.deleteDir(tmpDir);
		}
	}

	static private void writeEntryFile(String filename, int size, int strSize, List<FileIndexEntry> ent) {
		FileIndexWriter wr = new FileIndexWriter(filename, size, strSize);

		for (FileIndexEntry e : ent) {
			wr.writeEntry(e.pattern, e.range, e.shr);
		}

		wr.close();
	}

	static class HeapContainer implements Comparable<Object> {
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
