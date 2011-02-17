package sjtu.apex.gse.operator.test;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.operator.PatternPlan;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


public class PatternScanTest {

	List<Plan> plan;
	List<QuerySchema> qsAll;

	@Before
	public void setUp() throws Exception {
		qsAll = new ArrayList<QuerySchema>();
		plan = new ArrayList<Plan>();

		/* TEST CASE 1 */
//		{
//			QueryGraph g = new QueryGraph();
//			QueryGraphNode na, nb, nc;
//			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
//			na = g.addNode("metamodel");
//			nb = g.addNode("polici");
//			nc = g.addNode("xmln");
//			g.addEdge(na, nb, "hasMember");
//			g.addEdge(na, nc, "hasMember");
//			seln.add(na);
//			seln.add(nb);
//			seln.add(nc);
//			QuerySchema qs = new QuerySchema(g, seln); 
//			qsAll.add(qs);
//			plan.add(new PatternPlan(qs));
//		}

		/* TEST CASE 2 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("label");
			nb = g.addNode("histor");
			g.addEdge(na, nb, "genre");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsAll.add(qs);
			plan.add(new PatternPlan(qs, null));
		}

	}

	@Test
	public void testPlan() {
		long time = System.currentTimeMillis();
		for (int i = 0; i < plan.size(); i++) {
			Scan s = plan.get(i).open();

			while (s.next()) {
				for (int j = 0; j < qsAll.get(i).getSelectedNodeCount(); j++)
					System.out.print(s.getID(qsAll.get(i).getSelectedNode(j)) + " ");
				System.out.println();
			}
		}

		System.out.println(System.currentTimeMillis() - time);
	}

}
