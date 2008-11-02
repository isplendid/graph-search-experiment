package com.ibm.gse.search.test.metaweb;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.index.file.FileIndexEntry;
import com.ibm.gse.index.file.FileIndexReader;
import com.ibm.gse.query.MergeJoinPlan;
import com.ibm.gse.query.PatternPlan;
import com.ibm.gse.query.Plan;
import com.ibm.gse.query.Scan;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;
import com.ibm.gse.system.GraphStorage;

/**
 * 
 * @author Tian Yuan
 * 
 */
public class MetawebSearchTest {

	List<String> patterns;
	List<Plan> left;
	List<Plan> right;
	List<List<QueryGraphNode>> joinOn;
	List<Plan> plan;
	List<QuerySchema> qsAll;

	@Before
	public void setUp() throws Exception {
		patterns = new ArrayList<String>();
		plan = new ArrayList<Plan>();
		left = new ArrayList<Plan>();
		right = new ArrayList<Plan>();
		joinOn = new ArrayList<List<QueryGraphNode>>();
		qsAll = new ArrayList<QuerySchema>();

		FileIndexReader fir = new FileIndexReader(GraphStorage.config.getStringSetting("DataFolder", null) + "/index2", 3);
		FileIndexEntry fie;
		int counter = 0;

		while (fir.next()) {
			fie = fir.readEntry();
			patterns.add(fie.pattern);
			//			if ((counter++) > 10) break;
			QueryGraphNode na = null, nb = null, nc = null;

			QueryGraph g = GraphStorage.patternMan.getCodec().decodePattern(fie.pattern);
			for (int i = 0; i < g.nodeCount(); i++)
				if (g.getNode(i).getDegree() == 2)
					na = g.getNode(i);
				else if (nb == null)
					nb = g.getNode(i);
				else
					nc = g.getNode(i);

			List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
			seln.add(na);
			seln.add(nb);
			seln.add(nc);
			QuerySchema qs = new QuerySchema(g, seln); 
			qsAll.add(qs);

			/* PATTERN TEST */			

			plan.add(new PatternPlan(qs));

			/* MERGE JOIN TEST */
			{
				List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

				nl.add(na);
				nl.add(nb);
				QueryGraph gc = g.getInducedSubgraph(new HashSet<QueryGraphNode>(nl), null);
				QuerySchema qst = new QuerySchema(gc, nl);

				Plan pp = new PatternPlan(qst);
				left.add(pp);
			}

			{
				List<QueryGraphNode> nl = new ArrayList<QueryGraphNode>();

				nl.add(na);
				nl.add(nc);
				QueryGraph gc = g.getInducedSubgraph(new HashSet<QueryGraphNode>(nl), null);
				QuerySchema qst = new QuerySchema(gc, nl);

				Plan pp = new PatternPlan(qst);
				right.add(pp);
			}

			List<QueryGraphNode> jo = new ArrayList<QueryGraphNode>();
			jo.add(na);
			joinOn.add(jo);
		}

		fir.close();
	}

//	@Test
//	public void testFileRepository() throws IOException {
//		BufferedWriter wr = new BufferedWriter(new FileWriter("result.txt"));
//
//		for (int i = 0; i < left.size(); i++) {
//			long time;
//			Scan s;
//
//			wr.append(patterns.get(i) + "\t");
//
//			/* MERGE JOIN */
//			Plan p = new MergeJoinPlan(left.get(i), right.get(i), joinOn.get(i), qsAll.get(i));
//			time = System.currentTimeMillis();
//			s = p.open();
//
//			while (s.next()) {
//				for (int j = 0; j < qsAll.get(i).getSelectedNodeCount(); j++)
//					s.getID(qsAll.get(i).getSelectedNode(j));
//				//					wr.append.print(s.getID(qsAll.get(i).getSelectedNode(j)) + " ");
//				//				wr.append.println();
//			}
//			wr.append(System.currentTimeMillis() - time + "\t");
//			s.close();
//
//			/* PATTERN FETCH */
//			try {
//				time = System.currentTimeMillis();
//				s = plan.get(i).open();
//
//				while (s.next()) {
//					for (int j = 0; j < qsAll.get(i).getSelectedNodeCount(); j++)
//						s.getID(qsAll.get(i).getSelectedNode(j));
//					//					wr.append.print(s.getID(qsAll.get(i).getSelectedNode(j)) + " ");
//					//				wr.append.println();
//				}
//				wr.append(System.currentTimeMillis() - time + "\r\n");
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//				wr.append("\r\n");
//			}
//			s.close();
//
//		}
//		wr.close();
//	}
	
	@Test
	public void outputResultSize() throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter("resSize.txt"));
		
		for (int i = 0; i < left.size(); i++) {
			wr.append(patterns.get(i) + "\t");
			wr.append(GraphStorage.patternMan.getPatternInstanceCount(left.get(i).getSchema().getQueryGraph()) + "\t");
			wr.append(GraphStorage.patternMan.getPatternInstanceCount(right.get(i).getSchema().getQueryGraph()) + "\t");
			try {
				wr.append(GraphStorage.patternMan.getPatternInstanceCount(plan.get(i).getSchema().getQueryGraph()) + "\r\n");
			} catch (Exception e) {
				wr.append("\r\n");
			}
		}
		
		wr.close();
	}

}
