package sjtu.apex.gse.query;

import sjtu.apex.gse.struct.QuerySchema;

/**
 * 
 * @author Tian Yuan
 *
 */
public interface QueryReader {
	public QuerySchema read();
	public void close();
}
