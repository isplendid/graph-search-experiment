package sjtu.apex.gse.hash;

/**
 * This interface defines a hash function from string to 
 * 
 * @author Tian Yuan
 */
public interface HashFunction {
	
	/**
	 * Maps the input string to the output string
	 */
	public String hashStr(int str);
	
	/**
	 * Maps the input string to the output integer
	 */
	public Integer hashInt(int str);
}
