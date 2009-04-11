package sjtu.apex.gse.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.GraphStorage;


/**
 * 
 * 
 * @author Tian Yuan
 */
public class Experiment {
	
	final static boolean logPlan = true;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		QueryReader rd = new FileQueryReader(args[0]);
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		int cnt = 0;
		QuerySchema qs;
		
		while ((qs = rd.read()) != null) {
			System.out.println(++cnt);
			
			Plan p = GraphStorage.queryPlanner.plan(qs);
			
			if (logPlan) {
				FileWriter lw = new FileWriter("plan.log");
				lw.append(p.toString());
				lw.close();
			}
			
			long time = System.currentTimeMillis();
			Scan scan = p.open();

			int count = 0;
			while (scan.next()) {
				/* DO SOMETHING */
				count++;
			}
			wr.append((System.currentTimeMillis() - time) + "\t " + count + "\n");
		}
		
		File log = new File("plan.log");
		log.delete();
		
		wr.close();
	}

}
