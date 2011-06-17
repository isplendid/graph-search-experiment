package sjtu.apex.gse.exp.edge;

class EdgeContainer implements Comparable<Object> {
	int id;
	Edge edge;
	
	EdgeContainer(int id, Edge edge) {
		this.id = id;
		this.edge = edge;
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof EdgeContainer) {
			int res = id - ((EdgeContainer)arg0).id;
			
			if (res != 0) return res;
			
			return edge.toString().compareTo(((EdgeContainer)arg0).edge.toString());
		}
		else
			return 0;
	}
	
}
