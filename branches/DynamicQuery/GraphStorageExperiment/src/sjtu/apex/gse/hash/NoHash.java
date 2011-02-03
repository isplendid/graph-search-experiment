package sjtu.apex.gse.hash;

public class NoHash implements HashFunction {

	@Override
	public Integer hashInt(String str) {
		return null;
	}

	@Override
	public String hashStr(String str) {
		return str;
	}

}
