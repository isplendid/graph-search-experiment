package sjtu.apex.gse.exp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.visitor.SourcePlanVisitor;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.SPARQLQueryReader;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class ExperimentSrcNo {
	
	static boolean outResult = false;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String configFilename = args[0];
		String queryFilename = args[1];
		String resultFilename = args[2];
		String outputFilename = null;
		if (args.length >= 4) outputFilename = args[3];
		
		Configuration config = new FileConfig(configFilename);
		
		outResult = config.getIntegerSetting("OutResult", 0) == 0 ? false : true;
		
		QuerySystem sys = new QuerySystem(config);
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
			
			System.out.println(p.toString());
			
			if (out != null) {
				out.append(p.toString());
				out.flush();
			}

			int srcCnt = 0;
			
			SourcePlanVisitor spv = new SourcePlanVisitor();
			p.accept(spv);
			srcCnt = spv.getSourceSet().size();
			
			
			sys.close();
			if (out != null) out.close();
			
			wr.append(srcCnt + "\n");
			wr.close();	
		}
		
		//This is a workaround of some threads not being stopped
		System.exit(0);
	}

}
