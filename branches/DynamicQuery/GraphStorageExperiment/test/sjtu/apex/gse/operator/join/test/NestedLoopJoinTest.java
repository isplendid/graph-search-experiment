package sjtu.apex.gse.operator.join.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.operator.join.NestedLoopJoinPlan;
import sjtu.apex.gse.operator.web.WebPatternPlan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphEdge;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

public class NestedLoopJoinTest {
	List<Plan> left;
	List<Plan> right;
	List<List<QueryGraphNode>> joinOn;
	List<QuerySchema> qsAll;
	QuerySystem sys;

	@Before
	public void setUp() throws Exception {
		left = new ArrayList<Plan>();
		right = new ArrayList<Plan>();
		joinOn = new ArrayList<List<QueryGraphNode>>();
		qsAll = new ArrayList<QuerySchema>();
		
		sys = new QuerySystem(new FileConfig("cfg-web"));
		
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
		QueryGraphEdge e1, e2;
		
		List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
		na = g.addNode(sys.idManager().addGetID("<http://harth.org/andreas/foaf#ah>"));
		nb = g.addNode();
		nc = g.addNode();
		e1 = g.addEdge(na, nb, sys.idManager().addGetID("<http://xmlns.com/foaf/0.1/knows>"));
		e2 = g.addEdge(nb, nc, sys.idManager().addGetID("<http://xmlns.com/foaf/0.1/name>"));
		seln.add(nb);
		seln.add(nc);
		qsAll.add(new QuerySchema(g, seln));
		{
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(na);
			nl.add(nb);
			
			Set<QueryGraphEdge> es = new HashSet<QueryGraphEdge>();
			es.add(e1);
			
			QueryGraph gc = g.getInducedSubgraph(new HashSet<QueryGraphNode>(nl), es, null, null);
			QuerySchema qs = new QuerySchema(gc, nl);
			
			Plan pp = new WebPatternPlan(qs, sys, null);
			left.add(pp);
		}
		
		{
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(nb);
			nl.add(nc);
			
			Set<QueryGraphEdge> es = new HashSet<QueryGraphEdge>();
			es.add(e2);
			
			QueryGraph gc = g.getInducedSubgraph(new HashSet<QueryGraphNode>(nl), es, null, null);
			QuerySchema qs = new QuerySchema(gc, nl);
			
			Plan pp = new WebPatternPlan(qs, sys, null);
			right.add(pp);
		}
		
		List<QueryGraphNode> jo = new ArrayList<QueryGraphNode>();
		jo.add(nb);
		joinOn.add(jo);
		
	}

	@Test
	public void testOpen() {
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < left.size(); i++) {
			Plan p = new NestedLoopJoinPlan(left.get(i), right.get(i), joinOn.get(i), qsAll.get(i), sys);
			Scan s = p.open();
			
			while (s.next()) {
				for (int j = 0; j < qsAll.get(i).getSelectedNodeCount(); j++)
					System.out.print(sys.idManager().getURI(s.getID(qsAll.get(i).getSelectedNode(j))) + " ");
				System.out.println();
			}
		}
		
		System.out.println(System.currentTimeMillis() - time);
		sys.webRepository().close();
	}
}
