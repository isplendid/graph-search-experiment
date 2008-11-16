package com.ibm.gse.search.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.planner.Planner;
import com.ibm.gse.planner.dp.DynamicProgrammingPlanner;
import com.ibm.gse.query.Plan;
import com.ibm.gse.query.Scan;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;

public class QueryPlannerTest {
	
	Planner qp;
	List<QuerySchema> qsList;

	@Before
	public void setUp() throws Exception {
		qp = new DynamicProgrammingPlanner();
		qsList = new ArrayList<QuerySchema>();
		
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
//			qsList.add(qs);
//		}
		/* TEST 2 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("track");
			nb = g.addNode("progress");
			g.addEdge(na, nb, "genre");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
	}

	@Test
	public void testPlan() {
		for (QuerySchema qs : qsList) {
			Plan p = qp.plan(qs);
			Scan s = p.open();
			
			while (s.next()) {
				for (int i = 0; i < qs.getSelectedNodeCount(); i++)
					System.out.print(s.getID(qs.getSelectedNode(i)) + " ");
				System.out.println();
			}
		}
	}

}
