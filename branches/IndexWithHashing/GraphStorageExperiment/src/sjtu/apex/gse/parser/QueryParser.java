package sjtu.apex.gse.parser;

import sjtu.apex.gse.struct.QuerySchema;

/**
 * 
 * @author Tian Yuan
 *
 */
public interface QueryParser {
	public QuerySchema getNext();
	public void close();
}
