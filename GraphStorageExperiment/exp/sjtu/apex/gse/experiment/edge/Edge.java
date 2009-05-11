package sjtu.apex.gse.experiment.edge;

public class Edge {
	private String label;
	private String str;
	private boolean dir;
	
	public Edge(String tmp) {
		str = tmp;
		label = tmp.substring(1);
		dir = tmp.startsWith("+");
	}
	
	public Edge(String label, boolean dir) {
		this.label = label;
		this.dir = dir;
		this.str = ((dir ? "+" : "-") + label);
	}
	
	public String getLabel() {
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
