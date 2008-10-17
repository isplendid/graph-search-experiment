package com.ibm.gse.search.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.query.PatternPlan;
import com.ibm.gse.query.Plan;
import com.ibm.gse.query.Scan;
import com.ibm.gse.query.SortPlan;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;

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
			QueryGraphNode na = g.addNode("risk");
			QueryGraphNode nb = g.addNode("high");
			g.addEdge(na, nb, "related");
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(na);
			nl.add(nb);
			seln.add(nl);
			qs.add(new QuerySchema(g, nl));
		}

		/* TESTCASE 2 */
		{
			QueryGraph g = new QueryGraph();
			QueryGraphNode na = g.addNode("risk");
			QueryGraphNode nb = g.addNode("http");
			g.addEdge(na, nb, "related");
			List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

			nl.add(na);
			nl.add(nb);
			seln.add(nl);
			qs.add(new QuerySchema(g, nl));
		}
	}

	@Test
	public void testOpen() {
		for (int i = 0; i < qs.size(); i++) {

			Plan pp = new PatternPlan(qs.get(i));
			Plan sp = new SortPlan(pp, seln.get(i));

			Scan s = sp.open();
			
			System.out.println("RESULT " + (i + 1));
			
			while (s.next()) {
				for (QueryGraphNode n : seln.get(i))
					System.out.print(s.getID(n) + " ");
				System.out.println();
			}
			
			System.out.println();
		}
	}

}
