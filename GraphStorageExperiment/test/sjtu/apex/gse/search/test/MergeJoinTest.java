package sjtu.apex.gse.search.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.query.MergeJoinPlan;
import sjtu.apex.gse.query.PatternPlan;
import sjtu.apex.gse.query.Plan;
import sjtu.apex.gse.query.Scan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


public class MergeJoinTest {
	
	List<Plan> left;
	List<Plan> right;
	List<List<QueryGraphNode>> joinOn;
	List<QuerySchema> qsAll;

	@Before
	public void setUp() throws Exception {
		left = new ArrayList<Plan>();
		right = new ArrayList<Plan>();
		joinOn = new ArrayList<List<QueryGraphNode>>();
		qsAll = new ArrayList<QuerySchema>();
		
//		/* TEST CASE 1 */
//		QueryGraph g = new QueryGraph();
//		QueryGraphNode na, nb, nc;
//		List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
//		na = g.addNode("risk");
//		nb = g.addNode("high");
//		nc = g.addNode("http");
//		g.addEdge(na, nb, "related");
//		g.addEdge(na, nc, "related");
//		seln.add(na);
//		seln.add(nb);
//		seln.add(nc);
//		qsAll.add(new QuerySchema(g, seln));
//		{
//			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();
//
//			nl.add(na);
//			nl.add(nb);
//			QueryGraph gc = g.getNodeInducedSubgraph(new HashSet<QueryGraphNode>(nl));
//			QuerySchema qs = new QuerySchema(gc, nl);
//			
//			Plan pp = new PatternPlan(qs);
//			left.add(pp);
//		}
//		
//		{
//			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();
//
//			nl.add(na);
//			nl.add(nc);
//			QueryGraph gc = g.getNodeInducedSubgraph(new HashSet<QueryGraphNode>(nl));
//			QuerySchema qs = new QuerySchema(gc, nl);
//			
//			Plan pp = new PatternPlan(qs);
//			right.add(pp);
//		}
//		
//		List<QueryGraphNode> jo = new ArrayList<QueryGraphNode>();
//		jo.add(na);
//		joinOn.add(jo);
		
		/* TEST CASE 2 */
		QueryGraph g = new QueryGraph();
		QueryGraphNode na, nb, nc;
		List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
		na = g.addNode("metamodel");
		nb = g.addNode("polici");
		nc = g.addNode("xmln");
		g.addEdge(na, nb, "hasMember");
		g.addEdge(na, nc, "hasMember");
		seln.add(na);
		seln.add(nb);
		seln.add(nc);
		qsAll.add(new QuerySchema(g, seln));
		{
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(na);
			nl.add(nb);
			QueryGraph gc = g.getInducedSubgraph(new HashSet<QueryGraphNode>(nl), null);
			QuerySchema qs = new QuerySchema(gc, nl);
			
			Plan pp = new PatternPlan(qs);
			left.add(pp);
		}
		
		{
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(na);
			nl.add(nc);
			QueryGraph gc = g.getInducedSubgraph(new HashSet<QueryGraphNode>(nl), null);
			QuerySchema qs = new QuerySchema(gc, nl);
			
			Plan pp = new PatternPlan(qs);
			right.add(pp);
		}
		
		List<QueryGraphNode> jo = new ArrayList<QueryGraphNode>();
		jo.add(na);
		joinOn.add(jo);
		
	}

	@Test
	public void testOpen() {
		long time = System.currentTimeMillis();
		for (int i = 0; i < left.size(); i++) {
			Plan p = new MergeJoinPlan(left.get(i), right.get(i), joinOn.get(i), qsAll.get(i));
			Scan s = p.open();
			
			while (s.next()) {
				for (int j = 0; j < qsAll.get(i).getSelectedNodeCount(); j++)
					System.out.print(s.getID(qsAll.get(i).getSelectedNode(j)) + " ");
				System.out.println();
			}
		}
		
		System.out.println(System.currentTimeMillis() - time);
	}

}
