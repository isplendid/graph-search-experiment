package sjtu.apex.gse.hash;

import sjtu.apex.gse.config.Configuration;


/**
 * Class ModHash is a hash function based on modulo operation  
 * 
 * @author Yuan Tian
 */
public class ModHash implements HashFunction {
	int mod;
	final int wildcard = -1;
	final String wildcardStr = "*";
	
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
	public String hashStr(int str) {
		if (str == wildcard)
			return wildcardStr;
		else
			return Integer.toString(str % mod);
	}

	@Override
	public Integer hashInt(int str) {
		return str % mod;
	}

}
