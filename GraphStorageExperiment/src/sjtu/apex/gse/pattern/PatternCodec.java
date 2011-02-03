package sjtu.apex.gse.pattern;

import sjtu.apex.gse.struct.QueryGraph;

/**
 * This class contains a method that convert a structural graph
 * into a flattened string
 * @author Tian Yuan
 *
 */
public interface PatternCodec {
	
	/**
	 * Encode a structural pattern into a string 
	 * @param graph The structural pattern 
	 * @return The encoded string
	 */
	public String encodePattern(QueryGraph graph);
	
	/**
	 * Decode the pattern string into a structural pattern
	 * @param pattern The string containing the pattern
	 * @return The structural graph
	 */
	public QueryGraph decodePattern(String pattern);
}
