package com.ibm.gse.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.ibm.gse.indexer.IDManager;
import com.ibm.gse.indexer.LabelManager;
import com.ibm.gse.indexer.file.SleepyCatIDManager;
import com.ibm.gse.indexer.file.SleepyCatLabelManager;
import com.ibm.gse.storage.file.FileRepositoryReader;
import com.ibm.gse.storage.file.RecordRange;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.system.GraphStorage;

/**
 * 
 * @author Tian Yuan
 */
public class QueryGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		
		String dataFolder = GraphStorage.config.getStringSetting("DataFolder", null);
		IDManager idman = new SleepyCatIDManager();
		LabelManager labman = new SleepyCatLabelManager();
		String temp;
		
		while ((temp = rd.readLine()) != null) {
			if (temp.length() == 0) continue;
			RecordRange rr = GraphStorage.indexMan.seek(temp, 2);

			FileRepositoryReader fr = new FileRepositoryReader(dataFolder + "/storage1", 2, rr);
			QueryGraph g = GraphStorage.patternMan.getCodec().decodePattern(temp);
			Map<QueryGraphNode, Integer> map = GraphStorage.columnNodeMap.getMap(g);
			QueryGraphEdge edge = g.getEdge(0);
			int ent[];

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
						if (!l.equals(r) && Math.random() > 0.9999){
							wr.append("2 1\n");
							wr.append(l + "\n");
							wr.append(r + "\n");
							wr.append("1 2 " + pred + "\n");
						}
				}

			}
		}			
		
		rd.close();
		wr.close();

	}

}
