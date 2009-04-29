package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.system.GraphStorage;

/**
 * 
 * @author Yuan Tian
 *
 */
public class RandomEdgeSelection {
	
	static final int basis = 300000;
	
	public static void main(String[] args) throws IOException {
		FileIndexReader fir = new FileIndexReader(args[0], 2);
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		
		while (fir.next()) {
			if (Math.random() < ((double)fir.getInstanceCount() / basis) * 0.99 + 0.01) {
				QueryGraph g = GraphStorage.patternMan.getCodec().decodePattern(fir.getPatternString());
				
				wr.append(g.getEdge(0).getLabel() + "\t" + fir.getInstanceCount() + "\n");
			}
		}
		
		fir.close();
		wr.close();
	}
}
