package sjtu.apex.gse.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.SPARQLQueryReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;


/**
 * 
 * 
 * @author Tian Yuan
 */
public class Experiment {
	
	final static boolean logPlan = false;
	final static boolean outResult = false;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String configFilename = args[0];
		String queryFilename = args[1];
		String resultFilename = args[2];
		
		File bkf = new File("break");
		Configuration config = new FileConfig(configFilename);
		
		QuerySystem sys = new QuerySystem(config);
		QueryReader rd;
		
		if (queryFilename.endsWith(".sparql")) {
			rd = new SPARQLQueryReader(queryFilename, sys.idManager());
		} else {
			rd = new FileQueryReader(queryFilename, sys.idManager());
		}
		
		BufferedWriter wr;
		IDManager idman = null;
		int startId = -1;
		
		wr = new BufferedWriter(new FileWriter(resultFilename));
		
		if (outResult) idman = sys.idManager();
		
		int cnt = 0;
		QuerySchema qs;
		
		while (cnt < startId) {
			rd.read();
			cnt ++;
		}
		
		while ((qs = rd.read()) != null) {
			System.out.println(++cnt);
			
			Plan p = sys.queryPlanner().plan(qs);
			Set<Integer> srcs = new HashSet<Integer>();
			
			if (logPlan) {
				FileWriter lw = new FileWriter("plan.log");
				lw.append(p.toString());
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
				srcs.addAll(scan.getSourceSet());
				/* DO SOMETHING */
				count++;
			}
			scan.close();
			
			sys.close();
			ie.deri.urq.lidaq.benchmark.Benchmark bm = sys.webRepository().getBenchmark();
			long relSrc = (Long)bm.get(new String(ie.deri.urq.lidaq.benchmark.SourceLookupBenchmark.TOTAL_LOOKUPS)) - 
				(Long)bm.get(new String(ie.deri.urq.lidaq.benchmark.SourceLookupBenchmark.TOTAL_3XX_LOOKUPS));
			
			wr.append((System.currentTimeMillis() - time) + "\t " + count + "\t" + srcs.size() + "\t" + relSrc + "\n");
			
			File log = new File("plan.log");
			log.delete();
			
			wr.close();	
			bkf.delete();
			
			
			System.exit(0);
		}
		
	}
	
	

}
