package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.system.GraphStorage;

public class EdgeSelection {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FileIndexReader fir = new FileIndexReader(args[0], 2);
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		int threshold = Integer.parseInt(args[2]);
		
		while (fir.next()) {
			if (fir.getInstanceCount() >= threshold) {
				QueryGraph g = GraphStorage.patternMan.getCodec().decodePattern(fir.getPatternString());
				
				wr.append(g.getEdge(0).getLabel() + "\n");
			}
			
		}
		
		fir.close();
		wr.close();
	}

}
