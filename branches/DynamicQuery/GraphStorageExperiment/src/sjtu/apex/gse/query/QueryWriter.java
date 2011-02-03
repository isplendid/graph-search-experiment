package sjtu.apex.gse.query;

import sjtu.apex.gse.struct.QuerySchema;

/**
 * 
 * 
 * @author Tian Yuan
 *
 */
public interface QueryWriter {
	public void write(QuerySchema sch);
	public void close();
}
