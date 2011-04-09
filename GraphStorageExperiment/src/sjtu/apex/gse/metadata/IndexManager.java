package sjtu.apex.gse.metadata;

import java.util.Set;

import sjtu.apex.gse.storage.file.RecordRange;


/**
 *
 * @author Tian Yuan
 */
public interface IndexManager {

	/**
	 * 
	 * @param pattern
	 * @param size
	 * @return
	 */
	public RecordRange seek(String pattern, int size);
	
	/**
	 * 
	 * @param pattern
	 * @param size
	 * @return
	 */
	public int getPatternCount(String pattern, int size);
	
	/**
	 * 
	 * @param pattern
	 * @param size
	 * @return
	 */
	public Set<Integer> getSourceList(String pattern, int size);
	
	/**
	 * 
	 */
	public void close();
}
