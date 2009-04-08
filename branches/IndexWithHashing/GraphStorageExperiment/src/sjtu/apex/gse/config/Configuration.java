package sjtu.apex.gse.config;

/**
 * Configuration that stores detailed settings of the system 
 * @author Tian Yuan
 *
 */
public interface Configuration {
	
	/**
	 * Return the string value of a setting given its key
	 * @param key The key to the setting
	 * @param def The default value 
	 * @return The string value of the setting
	 */
	public String getStringSetting(String key, String def); 
	
	/**
	 * Return the integer value of a setting given its key 
	 * @param key The key to the setting
	 * @param def The default value
	 * @return The integer value of the setting
	 */
	public Integer getIntegerSetting(String key, Integer def);
	
	/**
	 * Return the double value of a setting given its key 
	 * @param key The key to the setting
	 * @param def The default value
	 * @return The double value of the setting
	 */
	public Double getDoubleSetting(String key, Double def);
}
