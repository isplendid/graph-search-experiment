package sjtu.apex.gse.exp.querygen;

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
	static double baseprob = 0.0001;
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new FileConfig(args[0]);
//		Configuration prob = new FileConfig(args[2]);
		QuerySystem sys = new QuerySystem(conf);
		basis = conf.getIntegerSetting("EdgeSelBasis", basis);
		baseprob = conf.getDoubleSetting("EdgeSelBaseProb", baseprob);
		
		FileIndexReader fir = new FileIndexReader(conf.getStringSetting("DataFolder", null) + "/index1", 2, sys.patternStrSize());
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		int cnt = 0;
		
		while (fir.next()) {
			if ((++cnt) % 10000 == 0)
				System.out.println(cnt);
			
			if (Math.random() < ((double)fir.getInstanceCount() / basis) * (1 - baseprob) + baseprob) {
				
				QueryGraph g = sys.patternManager().getCodec().decodePattern(fir.getPatternString());
				
				if (g.getEdge(0).getNodeFrom().isGeneral() && g.getEdge(0).getNodeTo().isGeneral()) {
					wr.append(sys.idManager().getURI(g.getEdge(0).getLabel()) + "\t" + fir.getInstanceCount() + "\n");
				}
			}
		}
		
		fir.close();
		wr.close();
	}
}
