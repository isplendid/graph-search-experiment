package sjtu.apex.gse.experiment.edge;

public class Edge {
	private String label;
	private boolean dir;
	
	public Edge(String label, boolean dir) {
		this.label = label;
		this.dir = dir;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean getDir() {
		return dir;
	}
	
	public int hashCode() {
		return ((dir ? "+" : "-") + label).hashCode();
	}
}
