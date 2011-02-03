package sjtu.apex.gse.nt;

public class Triple {
	String s, p, o;
	
	public Triple(String s, String p, String o) {
		this.s = s;
		this.p = p;
		this.o = o;
	}
	
	public String getSubject() {
		return s;
	}
	
	public String getObject() {
		return o;
	}
	
	public String getPredicate() {
		return p;
	}
}
