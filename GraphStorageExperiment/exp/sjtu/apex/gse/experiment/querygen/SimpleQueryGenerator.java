package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.storage.file.FileRepositoryReader;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.system.GraphStorage;


/**
 * SimpleQueryGenerator randomly generates only simple queries with one edge and two nodes
 * 
 * @author Tian Yuan
 */
public class SimpleQueryGenerator {
	
	static final double prob = 0.0001; 

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		
		String dataFolder = GraphStorage.config.getStringSetting("DataFolder", null);
		IDManager idman = new SleepyCatIDManager();
		LabelManager labman = new SleepyCatLabelManager();
		String temp;
		int count = 0;
		
		while ((temp = rd.readLine()) != null) {
			if (temp.length() == 0) continue;
			RecordRange rr = GraphStorage.indexMan.seek("*[+" + temp + "::*]", 2);

			FileRepositoryReader fr = new FileRepositoryReader(dataFolder + "/storage1", 2, rr);
			QueryGraph g = GraphStorage.patternMan.getCodec().decodePattern("*[+" + temp + "::*]");
			Map<QueryGraphNode, Integer> map = GraphStorage.columnNodeMap.getMap(g);
			QueryGraphEdge edge = g.getEdge(0);
			int ent[];

			BufferedWriter wr = null;
			while ((ent = fr.readEntry()) != null) {

				int ss = ent[map.get(edge.getNodeFrom())];
				int os = ent[map.get(edge.getNodeTo())];
				String sub = idman.getURI(ss);
				String pred = edge.getLabel();
				String obj = idman.getURI(os);

				String[] left = labman.getLabel(sub);
				String[] right = labman.getLabel(obj);

				if (left == null || right == null) {
					continue;
				}


				for (String l : left) {
					for (String r : right) 
						if (!l.equals(r) && Math.random() < (prob + (wr == null? 0.2 : 0))){
							if (wr == null) {
								wr = new BufferedWriter(new FileWriter(args[1] + "." + count));
								count++;
							}
							wr.append("2 1\n");
							wr.append(l + "\n");
							wr.append(r + "\n");
							wr.append("1 2 " + pred + "\n");
						}
				}

			}
			if (wr != null) wr.close();
		}			
		
		rd.close();
	}

}
