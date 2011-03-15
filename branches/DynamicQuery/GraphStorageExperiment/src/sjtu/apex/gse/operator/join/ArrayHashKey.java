package sjtu.apex.gse.operator.join;


public class ArrayHashKey {
	private int[] joinValue;
	
	public ArrayHashKey(int[] joinValue) {
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
			 int[] oValues = ((ArrayHashKey) o).joinValue;
			 
			 if (oValues.length == joinValue.length) {
				 ret = true;
				 
				 for (int i = 0; i < oValues.length; i++)
					 if (oValues[i] != joinValue[i])
						 ret = false;
			 } else {
				 System.err.println("Error length");
			 }
		} else {
			System.err.println("Error data type");
		}
		
		return ret;
	}
}