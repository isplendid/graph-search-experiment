package sjtu.apex.gse.planner.dp.test;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;
import sjtu.apex.gse.system.QuerySystem.QuerySystemMode;

public class DynamicProgrammingPlannerTest {

	private String configPath = "cfg-web-cmplx8";
	private String queryFile = "dbg/q/144q.86";
	
	private QuerySystem qs;
	private QueryReader qr;
	
	@Before
	public void setUp() throws Exception {
		qs = new QuerySystem(new FileConfig(configPath), QuerySystemMode.WEB_ONLY);
		qr = new FileQueryReader(queryFile, qs.idManager());
	}
	
	@Test
	public void test() throws Exception {
		QuerySchema sch;
		
		while ((sch = qr.read()) != null) {
			Plan p = qs.queryPlanner().plan(sch);
			
			System.out.println(p.toString());
		}
		
			
	}

}
