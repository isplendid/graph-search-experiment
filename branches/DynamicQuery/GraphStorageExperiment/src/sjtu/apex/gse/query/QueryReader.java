package sjtu.apex.gse.query;

import sjtu.apex.gse.struct.QuerySchema;

/**
 * 
 * @author Tian Yuan
 *
 */
public interface QueryReader {
	/**
	 * Get the next query schema in file
	 * 
	 * @return The next query schema if the reader does not reach EOF. null - Otherwise.
	 */
	public QuerySchema read();
	
	/**
	 * Close the reader
	 */
	public void close();
}
