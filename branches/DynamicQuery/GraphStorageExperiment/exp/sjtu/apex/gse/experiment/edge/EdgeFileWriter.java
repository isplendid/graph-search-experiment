package sjtu.apex.gse.experiment.edge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EdgeFileWriter {
	BufferedWriter wr;
	
	public EdgeFileWriter(String file) {
		try {
			wr = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void append(int id, Edge e) {
		try {
			wr.append(id + "\t" + e.toString() + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void close() {
		try {
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
