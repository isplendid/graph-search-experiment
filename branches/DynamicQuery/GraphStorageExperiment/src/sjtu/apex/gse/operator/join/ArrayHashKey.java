package sjtu.apex.gse.operator.join;


public class ArrayHashKey {
	private Integer[] joinValue;
	
	public ArrayHashKey(Integer[] joinValue) {
		this.joinValue = joinValue;
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		
		for (int ki : joinValue) {
			int highorder = h & 0xf8000000;
			h = h << 5;
			h = h ^ (highorder >> 27);
			h = h ^ ki;
		}
		
		return h;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean ret = false;
		
		if (o instanceof ArrayHashKey) {
			 Integer[] oValues = ((ArrayHashKey) o).joinValue;
			 
			 if (oValues.length == joinValue.length) {
				 ret = true;
				 
				 for (int i = 0; i < oValues.length; i++)
					 if (oValues[i] != joinValue[i])
						 ret = false;
			 }
		}
		
		return ret;
	}
}