package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.parser.FileQueryParser;
import sjtu.apex.gse.parser.QueryParser;
import sjtu.apex.gse.struct.GraphUtility;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.GraphStorage;

/**
 * ComplexQueryGenerator generates complex queries with more edges and nodes
 * 
 * @author Tian Yuan
 *
 */
public class ComplexQueryGenerator {
	final double pec = 0.3;
	final double pee = 0.1;
	
	String elfn;
	String initfn;
	LabelManager lm;
	IDManager im;
	

	public ComplexQueryGenerator(String elfn, String initfn) {
		this.elfn = elfn;
		this.initfn = initfn;
		lm = new SleepyCatLabelManager();
		im = new SleepyCatIDManager();
	}
	
	private QuerySchema getFullSchema(QueryGraph g) {
		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
		for (int j = g.nodeCount(); j >= 0; j--)
			nset.add(g.getNode(j));
		return new QuerySchema(g, nset);
	}
	
	private void constraintExtend(QuerySchema qs, List<QuerySchema> qarr) {
		QueryGraph qg = qs.getQueryGraph();
		
		for (int i = qg.nodeCount() - 1; i >= 0; i--)
			if (qg.getNode(i).isGeneral() && Math.random() < pec) {
				QueryGraphNode extNode = qg.getNode(i);
				List<String> avl = new ArrayList<String>();
				Scan s = GraphStorage.queryPlanner.plan(qs).open();
				
				while (s.next()) {
					int ni = s.getID(extNode);
					String[] sl = lm.getLabel(im.getURI(ni));
					for (int j = sl.length - 1; j >= 0; j--)
						avl.add(sl[j]);
				}
				
				QueryGraph ng = GraphUtility.extendConstraint(qg, i, avl.get((int)Math.round(avl.size() * Math.random())));
				
				qarr.add(getFullSchema(ng));
			}
	}
	
	private void addEdge(QuerySchema qs, List<QuerySchema> qarr, List<String> elabels) {
		QueryGraph qg = qs.getQueryGraph();
		int nodeCount = qg.nodeCount(), labelCount = elabels.size(); 
		Set<QueryGraphNode> usedNode = new HashSet<QueryGraphNode>();
		
		for (int i = nodeCount - 1; i >= 0; i--) {
			int toExt;
			while (!usedNode.contains(qg.getNode(toExt = (int)Math.round(Math.random() * nodeCount)))) ;
			
			Set<String>	testedLabel = new HashSet<String>();
			boolean found = false;
			
			while (!found) {
				String el = elabels.get((int)(labelCount * labelCount));
				boolean dir = (Math.random() > 0.5);
				
				QuerySchema nqs = getFullSchema(GraphUtility.extendEdge(qg, toExt, el, dir));
				Scan s = GraphStorage.queryPlanner.plan(nqs).open();
				while (s.next()) {
					found = true;
					break;
				}
				
				if (!found) {
					nqs = getFullSchema(GraphUtility.extendEdge(qg, toExt, el, dir));
					s = GraphStorage.queryPlanner.plan(nqs).open();
					while (s.next()) {
						found = true;
						break;
					}
					if (found) qarr.add(nqs);
				}
				else
					qarr.add(nqs);
				
				testedLabel.add(el);
			}
		}
	}

	public void generate(int threshold) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			QueryParser qp = new FileQueryParser(initfn);
			
			ArrayList<String> elabels = new ArrayList<String>();
			ArrayList<QuerySchema> qarr = new ArrayList<QuerySchema>();
			String temp;
			
			while ((temp = rd.readLine()) != null)
				elabels.add(temp);
			
			QuerySchema qs;
			
			while ((qs = qp.getNext()) != null)
				qarr.add(qs);
			
			int head = 0;
			
			while (head < qarr.size() && qarr.size() < threshold) {
				qs = qarr.get(head);
				
				addEdge(qs, qarr, elabels);
				constraintExtend(qs, qarr);
			}
			
			qp.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ComplexQueryGenerator qg = new ComplexQueryGenerator(args[0], args[1]);
		
		qg.generate(Integer.parseInt(args[2]));
	}

}
