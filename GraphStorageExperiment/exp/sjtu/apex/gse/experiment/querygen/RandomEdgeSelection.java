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
	
	static int basis = 200000;
	static double baseprob;
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new FileConfig(args[0]);
		Configuration prob = new FileConfig(args[2]);
		QuerySystem sys = new QuerySystem(conf);
		basis = prob.getIntegerSetting("EdgeSelBasis", 200000);
		baseprob = prob.getDoubleSetting("EdgeSelBaseProb", 0.0001);
		
		FileIndexReader fir = new FileIndexReader(conf.getStringSetting("DataFolder", null) + "/index1", 2, sys.patternStrSize());
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		
		while (fir.next()) {
			if (Math.random() < ((double)fir.getInstanceCount() / basis) * (1 - baseprob) + baseprob) {
				QueryGraph g = sys.patternManager().getCodec().decodePattern(fir.getPatternString());
				
				wr.append(g.getEdge(0).getLabel() + "\t" + fir.getInstanceCount() + "\n");
			}
		}
		
		fir.close();
		wr.close();
	}
}
