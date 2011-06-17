package sjtu.apex.gse.exp.edge;

public class Edge {
	private int label;
	private String str;
	private boolean dir;
	
	public Edge(String tmp) {
		label = Integer.parseInt(tmp.substring(1));
		dir = tmp.startsWith("+");
		str = tmp;
	}
	
	public Edge(int label, boolean dir) {
		this.label = label;
		this.dir = dir;
		this.str = ((dir ? "+" : "-") + Integer.toString(label));
	}
	
	public int getLabel() {
		return label;
	}
	
	public boolean getDir() {
		return dir;
	}
	
	public int hashCode() {
		return str.hashCode();
	}
	
	public String toString() {
		return str;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Edge)
			return str.equals(((Edge)o).str);
		else
			return false;
	}
}
