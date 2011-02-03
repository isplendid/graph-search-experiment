package sjtu.apex.gse.hash;

import sjtu.apex.gse.config.Configuration;


/**
 * Class ModHash is a hash function based on modulo operation  
 * 
 * @author Yuan Tian
 */
public class ModHash implements HashFunction {
	int mod;
	final String wildcat = "*";
	
	public ModHash(Configuration conf) {
		mod = conf.getIntegerSetting("HashMod", 13);
	}
	
	public ModHash(int mod) {
		this.mod = mod;
	}
	
	public int getModulo() {
		return mod;
	}

	@Override
	public String hashStr(String str) {
		if (str.equals(wildcat))
			return str;
		else
			return Integer.toString(str.hashCode() % mod);
	}

	@Override
	public Integer hashInt(String str) {
		return str.hashCode() % mod;
	}

}
