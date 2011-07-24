package sjtu.apex.gse.exp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.SPARQLQueryReader;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;
import sjtu.apex.gse.system.QuerySystem.QuerySystemMode;

public class ExperimentSrcNoBase {

	final static boolean logPlan = false;
	static boolean outResult = false;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String configFilename = args[0];
		String queryFilename = args[1];
		String resultFilename = args[2];
		String outputFilename = null;
		if (args.length >= 4) outputFilename = args[3];
		
		Configuration config = new FileConfig(configFilename);
		
		outResult = config.getIntegerSetting("OutResult", 0) == 0 ? false : true;
		
		QuerySystem sys = new QuerySystem(config, QuerySystemMode.FILE_ONLY);
		QueryReader rd;
		
		if (queryFilename.endsWith(".sparql")) {
			rd = new SPARQLQueryReader(queryFilename, sys.idManager());
		} else {
			rd = new FileQueryReader(queryFilename, sys.idManager());
		}
		
		BufferedWriter wr;
		BufferedWriter out = null;
		
		wr = new BufferedWriter(new FileWriter(resultFilename));
		if (outputFilename != null)
			out = new BufferedWriter(new FileWriter(outputFilename));
		
		int cnt = 0;
		QuerySchema qs;
		
		while ((qs = rd.read()) != null) {
			System.out.println(++cnt);
			
			Plan p = sys.queryPlanner().plan(qs);
			Set<Integer> srcs = new HashSet<Integer>();
			
			if (logPlan) {
				FileWriter lw = new FileWriter("plan.log");
				lw.append(p.toString());
				lw.close();
			}
			
			Scan scan = p.open();

			int count = 0;
			
			QueryGraph qg = qs.getQueryGraph();
			
			System.out.println(p.toString());
			
			if (out != null) {
				out.append(p.toString());
				out.flush();
			}
			
			while (scan.next()) {
				if (outResult) {
					for (int i = qg.nodeCount() - 1; i >= 0; i--)
						System.out.print(sys.idManager().getURI(scan.getID(qg.getNode(i))) + " ");
					System.out.println();
				}
				if (out != null) {
					for (int i = qg.nodeCount() - 1; i >= 0; i--)
						out.append(sys.idManager().getURI(scan.getID(qg.getNode(i))) + " ");
					out.append("\n");
				}
				srcs.addAll(scan.getSourceSet());
				/* DO SOMETHING */
				count++;
			}
			scan.close();
			sys.close();
			if (out != null) out.close();
			
			wr.append(count + "\t" + srcs.size() + "\n");
			
			new File("plan.log").delete();
			
			wr.close();	
		}
		
		//This is a workaround of some threads not being stopped
		System.exit(0);
		
	}

}
