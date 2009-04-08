package sjtu.apex.gse.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.parser.FileQueryParser;
import sjtu.apex.gse.parser.QueryParser;
import sjtu.apex.gse.query.Plan;
import sjtu.apex.gse.query.Scan;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.GraphStorage;


/**
 * 
 * 
 * @author Tian Yuan
 */
public class Experiment {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		QueryParser rd = new FileQueryParser(args[0]);
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		int cnt = 0;
		QuerySchema qs;
		
		while ((qs = rd.getNext()) != null) {
			System.out.println(++cnt);
			
			Plan p = GraphStorage.queryPlanner.plan(qs);
			long time = System.currentTimeMillis();
			Scan scan = p.open();

			int count = 0;
			while (scan.next()) {
				/* DO SOMETHING */
				count++;
			}
			wr.append((System.currentTimeMillis() - time) + "\t " + count + "\n");
		}
		wr.close();
	}

}
