package sjtu.apex.gse.debug;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class Bug20090503p1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuerySystem sys = new QuerySystem(new FileConfig(args[0]));
		
		QueryGraph g = new QueryGraph();
		
		g.addEdge(g.addNode(), g.addNode("4", true), "genre");
		
		Plan p = sys.queryPlanner().plan(new QuerySchema(g, g.getNodeSet()));
		
		Scan s = p.open();
		
		while (s.next()) {
			
		}

	}

}
