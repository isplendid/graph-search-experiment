package sjtu.apex.gse.indexer.file;

import sjtu.apex.gse.index.file.FileIndexWriter;
import sjtu.apex.gse.storage.file.FileRepositoryWriter;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.temp.file.TempFileEntry;
import sjtu.apex.gse.temp.file.TempRepositoryFileReader;


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
		
		if (currentPat != null) iwr.writeEntry(currentPat, new RecordRange(start, last));
		
		srd.close();
		dwr.close();
		iwr.close();
	}
}
