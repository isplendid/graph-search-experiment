package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.index.file.FileIndexReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.system.QuerySystem;

/**
 * 
 * @author Yuan Tian
 *
 */
public class RandomEdgeSelection {
	
	static final int basis = 300000;
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new FileConfig(args[0]);
		QuerySystem sys = new QuerySystem(conf);
		
		FileIndexReader fir = new FileIndexReader(args[1], 2, sys.patternStrSize());
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[2]));
		
		while (fir.next()) {
			if (Math.random() < ((double)fir.getInstanceCount() / basis) * 0.99 + 0.01) {
				QueryGraph g = sys.patternManager().getCodec().decodePattern(fir.getPatternString());
				
				wr.append(g.getEdge(0).getLabel() + "\t" + fir.getInstanceCount() + "\n");
			}
		}
		
		fir.close();
		wr.close();
	}
}
