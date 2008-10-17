package com.ibm.gse.config;

import java.io.FileReader;
import java.util.Properties;

/**
 * A wrap-up of java Properties, implementing the local configuration interface 
 * @author Tian Yuan
 *
 */
public class FileConfig implements Configuration {
	
	Properties prop = new Properties();
	
	public FileConfig(String filename) {
		try {
			prop.load(new FileReader(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer getIntegerSetting(String key, Integer def) {
		try {
			return  Integer.parseInt(prop.getProperty(key));
		} catch (Exception e) {
			return def;
		}
	}

	public Double getDoubleSetting(String key, Double def) {
		try {
			return Double.valueOf(prop.getProperty(key));
		} catch (Exception e) {
			return def;
		}
	}

	public String getStringSetting(String key, String def) {
		return prop.getProperty(key, def); 
	}

}
