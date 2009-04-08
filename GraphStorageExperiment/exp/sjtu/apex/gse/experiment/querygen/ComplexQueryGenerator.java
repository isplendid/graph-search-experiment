package sjtu.apex.gse.experiment.querygen;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.indexer.LabelManager;

/**
 * ComplexQueryGenerator generates complex queries with more edges and nodes
 * 
 * @author Tian Yuan
 *
 */
public class ComplexQueryGenerator {
	Configuration conf;
	String eFile;
	String nFile;
	
	public ComplexQueryGenerator(String config) {
		conf = new FileConfig(config);
		
		eFile = conf.getStringSetting("EdgeLabels", null);
		nFile = conf.getStringSetting("LabelRepository", null);
	}
	
	public void generate() {
		LabelManager lm = new Sleepycat ; 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
