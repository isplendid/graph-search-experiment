package sjtu.apex.gse.search.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.query.PatternPlan;
import sjtu.apex.gse.query.Plan;
import sjtu.apex.gse.query.Scan;
import sjtu.apex.gse.query.SortPlan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


public class SortTest {

	List<QuerySchema> qs;
	List<List<QueryGraphNode>> seln;

	@Before
	public void setUp() throws Exception {
		qs = new ArrayList<QuerySchema>();
		seln = new ArrayList<List<QueryGraphNode>>();


		/* TESTCASE 1 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na = g.addNode();
			QueryGraphNode nb = g.addNode();
			g.addEdge(na, nb, "22-rdf-syntax-ns#type");
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(na);
			nl.add(nb);
			seln.add(nl);
			qs.add(new QuerySchema(g, nl));
		}

		/* TESTCASE 2 */
//		{
//			QueryGraph g = new QueryGraph();
//			QueryGraphNode na = g.addNode("risk");
//			QueryGraphNode nb = g.addNode("http");
//			g.addEdge(na, nb, "related");
//			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();
//
//			nl.add(na);
//			nl.add(nb);
//			seln.add(nl);
//			qs.add(new QuerySchema(g, nl));
//		}
	}

	@Test
	public void testOpen() {
		for (int i = 0; i < qs.size(); i++) {

			Plan pp = new PatternPlan(qs.get(i));
			Plan sp = new SortPlan(pp, seln.get(i));

			Scan s = sp.open();
			
			System.out.println("RESULT " + (i + 1));
			
			int count = 0;
			while (s.next()) {
				count ++;
				if (s.getID(0) == 4159) {
					for (QueryGraphNode n : seln.get(i))
						System.out.print(s.getID(n) + " ");
					System.out.println("count = " + count);
				}
			}
			
			System.out.println();
		}
	}

}