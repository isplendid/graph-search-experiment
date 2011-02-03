package sjtu.apex.gse.debug;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class Bug20090514p1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuerySystem sys = new QuerySystem(new FileConfig("cfg-lubm50-7"));
		
		QueryGraph g = new QueryGraph();
		
		QueryGraphNode a = g.addNode("0", true);
		QueryGraphNode b = g.addNode("0", true);
		QueryGraphNode c = g.addNode();
		
		g.addEdge(a, b, "takescourse");
		g.addEdge(c, a, "publicationauthor");
		
		Plan p = sys.queryPlanner().plan(new QuerySchema(g, g.getNodeSet()));
		
		Scan s = p.open();
		
		int cnt = 0;
		while (s.next()) {
			cnt++;
		}
		
		System.out.println("cnt = " + cnt);
		s.close();
	}

}
