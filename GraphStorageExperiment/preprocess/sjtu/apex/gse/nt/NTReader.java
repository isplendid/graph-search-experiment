package sjtu.apex.gse.nt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class NTReader {
	
	BufferedReader rd;
	Triple tri;
	
	public NTReader(String filename) {
		try {
			rd = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean next() {
		String temp;
		
		try {
			if ((temp = rd.readLine()) == null) return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		String[] ts = temp.split("[ \t]+");
		
		if (ts.length < 3)
			return false;
		
		tri = new Triple(ts[0], ts[1], ts[2]);
		
		return true;
	}
	
	public Triple getTriple() {
		return tri;
	}
	
	public void close() {
		try {
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
