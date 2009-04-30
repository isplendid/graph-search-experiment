package sjtu.apex.gse.search.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.planner.Planner;
import sjtu.apex.gse.planner.dp.DynamicProgrammingPlanner;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


public class QueryPlannerTest {
	
	Planner qp;
	List<QuerySchema> qsList;

	@Before
	public void setUp() throws Exception {
		qp = new DynamicProgrammingPlanner(null);
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
		
		/* TEST 3 */
//		{
//			QueryGraph g = new QueryGraph();
//			QueryGraphNode na, nb;
//			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
//			na = g.addNode("GraduateStudent");
//			nb = g.addNode("GraduateStudent0");
//			g.addEdge(nb, na, "22-rdf-syntax-ns#type");
//			seln.add(na);
//			seln.add(nb);
//			QuerySchema qs = new QuerySchema(g, seln); 
//			qsList.add(qs);
//		}
		
		/* TEST 4 */
//		{
//			QueryGraph g = new QueryGraph();
//            QueryGraphNode na, nb, nc;
//            List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
//            na = g.addNode("GraduateStudent");
//            nb = g.addNode("GraduateCourse0");
//            nc = g.addNode();
//            g.addEdge(nc, na, "22-rdf-syntax-ns#type");
//            g.addEdge(nc, nb, "univ-bench.owl#takesCourse");
//            seln.add(na);
//            seln.add(nb);
//            seln.add(nc);
//            QuerySchema qs = new QuerySchema(g, seln); 
//            qsList.add(qs);
//
//		}

		
//		/* TEST 5 */
//		{
//			QueryGraph g = new QueryGraph();
//			QueryGraphNode na, nb, nc;
//			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
//			na = g.addNode("GraduateStudent");
//			nb = g.addNode();
//			nc = g.addNode("GraduateCourse0");
//			g.addEdge(nb, na, "22-rdf-syntax-ns#type");
//			g.addEdge(nb, nc, "univ-bench.owl#takesCourse");
//			seln.add(na);
//			seln.add(nb);
//			seln.add(nc);
//			QuerySchema qs = new QuerySchema(g, seln); 
//			qsList.add(qs);
//		}
		
		/* TEST 6 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb, nc;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("comic");
			nb = g.addNode();
			g.addEdge(nb, na, "genre");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 7 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb, nc;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("japanes");
			nb = g.addNode();
			g.addEdge(nb, na, "publisher");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}

		/* TEST 8 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb, nc;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("comic");
			nb = g.addNode();
			nc = g.addNode("japanes");
			g.addEdge(nb, na, "genre");
			g.addEdge(nb, nc, "publisher");
			seln.add(na);
			seln.add(nb);
			seln.add(nc);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 9*/
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("atlanta");
			nb = g.addNode();
			g.addEdge(nb, na, "birthPlace");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 10 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("atlanta");
			nb = g.addNode("eric");
			g.addEdge(nb, na, "birthPlace");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 11 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb, nc;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode();
			nb = g.addNode("eric");
			nc = g.addNode();
			g.addEdge(nb, na, "birthPlace");
			g.addEdge(nb, nc, "trainer");
			seln.add(na);
			seln.add(nb);
			seln.add(nc);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 12 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("martial");
			nb = g.addNode();
			g.addEdge(nb, na, "starring");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 13 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("martial");
			nb = g.addNode("action");
			g.addEdge(nb, na, "starring");
			seln.add(na);
			seln.add(nb);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
		
		/* TEST 14 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na, nb, nc;
			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			na = g.addNode("martial");
			nb = g.addNode("action");
			nc = g.addNode();
			g.addEdge(nb, na, "starring");
			g.addEdge(nb, nc, "director");
			seln.add(na);
			seln.add(nb);
			seln.add(nc);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsList.add(qs);
		}
	}

	@Test
	public void testPlan() {
		for (QuerySchema qs : qsList) {
			Plan p = qp.plan(qs);
			
			long time = System.currentTimeMillis();
			
			Scan s = p.open();
			
			while (s.next()) {
				for (int i = 0; i < qs.getSelectedNodeCount(); i++)
					s.getID(qs.getSelectedNode(i));
//					System.out.print(s.getID(qs.getSelectedNode(i)) + " ");
//				System.out.println();
			}
			
			System.out.println(System.currentTimeMillis() - time);
		}
	}
}
