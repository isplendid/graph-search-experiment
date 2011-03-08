package sjtu.apex.gse.hash;

public class NoHash implements HashFunction {
	
	final int wildcard = -1;
	final String wildcardStr = "*";

	@Override
	public Integer hashInt(int str) {
		return str;
	}

	@Override
	public String hashStr(int str) {
		if (str == wildcard)
			return wildcardStr;
		else
			return Integer.toString(str);
	}

}
