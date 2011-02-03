package sjtu.apex.gse.nt;

import java.util.Comparator;

public class NTComparator implements Comparator<Triple> {
	
	int[] ord;
	
	public NTComparator() {
		this("SPO");
	}
	
	public NTComparator(String order) {
		ord = new int[order.length()];
		
		for (int i = 0; i < ord.length; i++)
			switch (order.charAt(i)) {
			case 'S' : ord[i] = 0;break;
			case 'P' : ord[i] = 1;break;
			case 'O' : ord[i] = 2;break;
			default : ord[i] = -1;
			}
	}

	@Override
	public int compare(Triple arg0, Triple arg1) {
		String s0 = null, s1 = null;
		for (int i = 0; i < ord.length; i++) {
			switch (ord[i]) {
			case 0 : s0 = arg0.getSubject(); s1 = arg1.getSubject(); break;
			case 1 : s0 = arg0.getPredicate(); s1  = arg1.getPredicate(); break;
			case 2 : s0 = arg0.getObject(); s1  = arg1.getObject(); break;
			}
			
			int res = s0.compareTo(s1);
			
			if (res != 0) return res;
		}
		return 0;
	}

}
