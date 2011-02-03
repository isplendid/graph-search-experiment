package sjtu.apex.gse.nt;


public class NTReaderContainer implements Comparable<Object> {
	
	NTReader r;
	NTComparator cmp;
	
	public NTReaderContainer(NTReader r, NTComparator cmp) {
		this.r = r;
		this.cmp = cmp;
	}
	
	public NTReader getReader() {
		return r;
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof NTReaderContainer) {
			return -cmp.compare(r.getTriple(), ((NTReaderContainer) arg0).r.getTriple());
		} else
			return 0;
	}

}
