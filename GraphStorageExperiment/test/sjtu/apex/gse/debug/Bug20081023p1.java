package sjtu.apex.gse.debug;

import sjtu.apex.gse.temp.file.TempFileEntry;
import sjtu.apex.gse.temp.file.TempRepositoryFileReader;

/**
 * Bug 20081023-1 : Read in temp repository file error.
 * 
 * Exception in thread "main" java.lang.StringIndexOutOfBoundsException: String index out of range: -69
 *	at java.lang.String.checkBounds(String.java:398)
 *	at java.lang.String.<init>(String.java:570)
 *	at com.ibm.gse.temp.file.TempRepositoryFileReader.readRecord(TempRepositoryFileReader.java:53)
 *	at com.ibm.gse.temp.file.util.TempRepositorySorter.sort(TempRepositorySorter.java:37)
 *	at com.ibm.gse.temp.file.util.TempRepositorySorter.sort(TempRepositorySorter.java:23)
 *	at com.ibm.gse.indexer.file.FileIndexService.indexEdge(FileIndexService.java:138)
 *	at com.ibm.gse.indexer.file.FileIndexService.main(FileIndexService.java:353)
 * @author Tian Yuan
 * 
 */
public class Bug20081023p1 {

	public static void main(String[] args) {
		TempRepositoryFileReader trr = new TempRepositoryFileReader("H:/Barn/data/dbpedia/raw/raw1", 2, 128);
		TempFileEntry tfe = null;
		
//		try {
			while ((tfe = trr.readRecord()) != null) ;
		
//		} catch (Exception e) {
//			System.out.println(tfe.pattern);
//			
//		}
	}
}
