package sjtu.apex.gse.planner.dp.test;


import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;
import sjtu.apex.gse.system.QuerySystem.QuerySystemMode;

public class DynamicProgrammingPlannerTest {

	private String configPath = "cfg-test";
	private String queryFile = "dbg/test.q";
	
	private QuerySystem qs;
	private QueryReader qr;
	
	@Before
	public void setUp() throws Exception {
		qs = new QuerySystem(new FileConfig(configPath), QuerySystemMode.FILE_ONLY);
		qr = new FileQueryReader(queryFile, qs.idManager());
	}
	
	@Test
	public void test() throws Exception {
		QuerySchema sch;
		
		while ((sch = qr.read()) != null) {
			Plan p = qs.queryPlanner().plan(sch);
			Scan s = p.open();
			
			while (s.next()) {
				System.out.println("{");
				for (QueryGraphEdge e : sch.getQueryGraph().getEdgeSet())
					System.out.println(qs.idManager().getURI(s.getID(e.getNodeFrom())) + " " + qs.idManager().getURI(e.getLabel()) + " " + qs.idManager().getURI(s.getID(e.getNodeTo())));
				System.out.println("}");
			}
			
			s.close();
		}
		
			
	}

}
