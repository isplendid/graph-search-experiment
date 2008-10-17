package com.ibm.gse.indexer.file;

import com.ibm.gse.storage.file.FileRepositoryWriter;
import com.ibm.gse.storage.file.RID;
import com.ibm.gse.storage.file.RecordRange;
import com.ibm.gse.temp.file.TempFileEntry;
import com.ibm.gse.temp.file.TempRepositoryFileReader;

/**
 *
 * @author Tian Yuan
 * 
 */
public class FileIndexer {
	
	/**
	 * This method takes in a sorted temp repository file and generate
	 * a repository file and its index
	 * @param src
	 * @param dest
	 * @param idx 
	 * @param size
	 */
	static public void index(String src, String dest, String idx, int size) {
		TempRepositoryFileReader srd = new TempRepositoryFileReader(src, size);
		FileRepositoryWriter dwr = new FileRepositoryWriter(dest, size);
		FileIndexWriter iwr = new FileIndexWriter(idx, size);
		TempFileEntry tfe;
		RID start = null, last = null;
		String currentPat = null;
		
		while ((tfe = srd.readRecord()) != null) {
			if (currentPat == null) {
				start = dwr.getRID();
				currentPat = tfe.pattern;
			}
			else if (!currentPat.equals(tfe.pattern)) {
				iwr.writeEntry(currentPat, new RecordRange(start, last));
				start = dwr.getRID();
				currentPat = tfe.pattern;
			}
			
			last = dwr.getRID();
			dwr.writeEntry(tfe.ins);
		}
		
		iwr.writeEntry(currentPat, new RecordRange(start, last));
		srd.close();
		dwr.close();
		iwr.close();
	}
}
