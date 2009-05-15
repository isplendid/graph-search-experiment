package sjtu.apex.gse.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;


/**
 * 
 * 
 * @author Tian Yuan
 */
public class Experiment {
	
	final static boolean breakRestart = true;
	final static boolean logPlan = false;
	final static boolean outResult = false;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File bkf = new File("break");
		Configuration config = new FileConfig(args[0]);
		QuerySystem sys = new QuerySystem(config);
		QueryReader rd = new FileQueryReader(args[1]);
		BufferedWriter wr;
		IDManager idman = null;
		int startId = -1;
		
		if (breakRestart && bkf.exists()) {
			BufferedReader bkr = new BufferedReader(new FileReader(bkf));

			startId = Integer.parseInt(bkr.readLine());
			bkr.close();
			bkf.delete();
			wr = new BufferedWriter(new FileWriter(args[2], true));
			wr.append("#\n");
		}
		else
			wr = new BufferedWriter(new FileWriter(args[2]));
		
		if (outResult) idman = new SleepyCatIDManager(config);
		
		int cnt = 0;
		QuerySchema qs;
		
		while (cnt <= startId) {
			rd.read();
			cnt ++;
		}
		
		while ((qs = rd.read()) != null) {
			System.out.println(++cnt);
			
			Plan p = sys.queryPlanner().plan(qs);
			
			if (logPlan) {
				FileWriter lw = new FileWriter("plan.log");
				lw.append(p.toString());
				lw.close();
			}
			
			if (breakRestart) {
				FileWriter lw = new FileWriter("break");
				lw.append(Integer.toString(cnt));
				lw.close();
			}
			
			long time = System.currentTimeMillis();
			Scan scan = p.open();

			int count = 0;
			QueryGraph qg = qs.getQueryGraph();
			while (scan.next()) {
				if (outResult) {
					for (int i = qg.nodeCount() - 1; i >= 0; i--)
						System.out.print(idman.getURI(scan.getID(qg.getNode(i))) + " ");
					System.out.println();
				}
				/* DO SOMETHING */
				count++;
			}
			scan.close();
			
			wr.append((System.currentTimeMillis() - time) + "\t " + count + "\n");
			if (breakRestart) wr.flush();
		}
		
		if (outResult) idman.close();
		
		File log = new File("plan.log");
		log.delete();
		
		bkf.delete();
		
		wr.close();
	}

}
