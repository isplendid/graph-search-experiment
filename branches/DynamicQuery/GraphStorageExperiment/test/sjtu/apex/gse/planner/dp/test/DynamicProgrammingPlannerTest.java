package sjtu.apex.gse.planner.dp.test;


import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.planner.dp.DynamicProgrammingPlanner;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class DynamicProgrammingPlannerTest {

	private String configPath = "cfg-test";
	private String queryFile = "tmp/q/q1";
	
	private DynamicProgrammingPlanner dpp;
	private QuerySystem qs;
	private QueryReader qr;
	
	@Before
	public void setUp() throws Exception {
		qs = new QuerySystem(new FileConfig(configPath));
		dpp = new DynamicProgrammingPlanner(qs);
		qr = new FileQueryReader(queryFile, qs.idManager());
	}
	
	@Test
	public void test() throws Exception {
		QuerySchema sch;
		
		while ((sch = qr.read()) != null) {
			Plan p = dpp.plan(sch);
			Scan s = p.open();
			
			while (s.next()) {
				System.out.println();
				for (QueryGraphNode n : sch.getSelectedNodeSet())
					System.out.println(qs.idManager().getURI(s.getID(n)) + " ");
			}
			
			s.close();
		}
		
			
	}

}
