package sjtu.apex.gse.exp.edge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EdgeFileReader implements Comparable<Object>{
	BufferedReader rd;
	int id;
	Edge edge;
	
	public EdgeFileReader(String file) {
		try {
			rd = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean next() {
		String temp;
		
		try {
			temp = rd.readLine();
			
			if (temp == null)
				return false;
			
			String[] ts;
			
			ts = temp.split("\t");
			if (ts.length == 2) {
				id = Integer.parseInt(ts[0]);
				edge = new Edge(ts[1]);
				return true;
			}
			else
				return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getID() {
		return id;
	}
	
	public Edge getEdge() {
		return edge;
	}
	
	public void close() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof EdgeFileReader) {
			int res = id - ((EdgeFileReader)o).id; 
			
			if (res != 0)
				return -res;
			
			return -edge.toString().compareTo(((EdgeFileReader)o).edge.toString());
		} else
			return 0;
	}
}
