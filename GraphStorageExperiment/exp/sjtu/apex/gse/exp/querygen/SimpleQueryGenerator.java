package sjtu.apex.gse.exp.querygen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.storage.file.FileRepositoryEntry;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.system.QuerySystem;


/**
 * SimpleQueryGenerator randomly generates only simple queries with one edge and two nodes
 * 
 * @author Tian Yuan
 */
public class SimpleQueryGenerator {
	
	static double prob = 0.0001;
	static double base = 0.2;

	/**
	 * @param args A list of filenames which in order represent the config file for the system, edge candidate, 
	 * output file and the config file for the query generator.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Configuration config = new FileConfig(args[0]);
		prob = config.getDoubleSetting("SimpleGenExt", prob);
		base = config.getDoubleSetting("SimpleGenBase", base);
		QuerySystem sys = new QuerySystem(config);
		BufferedReader rd = new BufferedReader(new FileReader(args[1]));
		
		String dataFolder = sys.config().getStringSetting("DataFolder", null);
		IDManager idman = sys.idManager();
//		LabelManager labman = new SleepyCatLabelManager(sys.config());
		String temp;
		
		int fldrcnt = 0;
		while ((temp = rd.readLine()) != null) {
			FilesystemUtility.createDir(args[2] + "/" + fldrcnt);
			
			if (temp.length() == 0) continue;
			String p = temp.split("\t")[0];
			RecordRange rr = sys.indexManager().seek("*[+" + Integer.toString(idman.getID(p)) + "::*]", 2);

			FileRepositoryReader fr = new FileRepositoryReader(dataFolder + "/storage1", 2, rr);
			QueryGraph g = sys.patternCodec().decodePattern("*[+" + Integer.toString(idman.getID(p)) + "::*]");
			Map<QueryGraphNode, Integer> map = sys.columnNodeMap().getMap(g);
			QueryGraphEdge edge = g.getEdge(0);
			FileRepositoryEntry ent;

			BufferedWriter wr = null;
			int filecount = 0;
			
			while ((ent = fr.readEntry()) != null) {
				
				if (Math.random() < (prob + (wr == null? base : 0))) {
					int ss = ent.bindings[map.get(edge.getNodeFrom())];
					int os = ent.bindings[map.get(edge.getNodeTo())];
					String sub, obj;
					String pred = idman.getURI(edge.getLabel());
					
					if (Math.random() < 0.5) {
						sub = idman.getURI(ss);
						obj = "*";
					}
					else {
						sub = "*";
						obj = idman.getURI(os);
					}
					
					wr = new BufferedWriter(new FileWriter(args[2] + "/" + fldrcnt + "/q." + filecount));
					
					wr.append("2 1\n");
					wr.append(sub + "\n");
					wr.append(obj + "\n");
					wr.append("1 2 " + pred + "\n");

					wr.close();
					filecount++;
				}
			}
			fr.close();
			fldrcnt ++;
		}			
		
		rd.close();
		sys.close();
	}

}
