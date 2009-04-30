package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.system.QuerySystem;

/**
 * This class selects edges with instances above a threshold
 * 
 * @author Yuan Tian
 *
 */
public class ThresholdEdgeSelection {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		QuerySystem sys = new QuerySystem(new FileConfig(args[0]));
		
		FileIndexReader fir = new FileIndexReader(args[1], 2, sys.patternStrSize());
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[2]));
		int threshold = Integer.parseInt(args[3]);
		
		while (fir.next()) {
			if (fir.getInstanceCount() >= threshold) {
				QueryGraph g = sys.patternManager().getCodec().decodePattern(fir.getPatternString());
				
				wr.append(g.getEdge(0).getLabel() + "\n");
			}
			
		}
		
		fir.close();
		wr.close();
	}

}
