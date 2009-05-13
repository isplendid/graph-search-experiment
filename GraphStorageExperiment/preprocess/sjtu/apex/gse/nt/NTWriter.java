package sjtu.apex.gse.nt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class NTWriter {
	
	BufferedWriter wr;
	
	public NTWriter(String filename) {
		try {
			wr = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void write(Triple t) {
		try {
			wr.append(t.s + "\t" + t.p + "\t" + t.o + "\n");
		} catch (IOException e) {
			e.printStackTrace();
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
