package sjtu.apex.gse.debug;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class Bug20090522p1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashFunction f = new ModHash(7);
		// TODO Auto-generated method stub
		Configuration conf = new FileConfig("cfg-dbp-r");
		QuerySystem sys = new QuerySystem(conf);
		LabelManager lbman = new SleepyCatLabelManager(conf);
		IDManager idman = new SleepyCatIDManager(conf);
		
		QueryGraph g = new QueryGraph();
		
		QueryGraphNode a = g.addNode();
		QueryGraphNode b = g.addNode();
//		QueryGraphNode c = g.addNode();
		
		g.addEdge(a, b, "city07");
//		g.addEdge(c, a, "publicationauthor");
		
		Plan p = sys.queryPlanner().plan(new QuerySchema(g, g.getNodeSet()));
		
		Scan s = p.open();
		
		int cnt = 0;
		while (s.next()) {
			int ss = s.getID(a);
			int os = s.getID(b);
			
			String[] sl = lbman.getLabel(idman.getURI(ss));
			String[] ol = lbman.getLabel(idman.getURI(os));
			
			for (String ta : sl)
				for (String tb : ol)
					if (f.hashInt(ta) == 0 && f.hashInt(tb) == 2)
						System.out.println(ss + "," + os);
			
			cnt++;
		}
		
		System.out.println("cnt = " + cnt);
		s.close();
	}

}
