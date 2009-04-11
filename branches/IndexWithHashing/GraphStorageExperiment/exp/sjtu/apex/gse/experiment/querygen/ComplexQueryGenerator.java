package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.File;
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
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.FileQueryWriter;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.QueryWriter;
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
	final double pee = 0.7;
	final double propEdgeCheck = 0.5;
	final boolean logging = true;
	
	String elfn;
	String initfn;
	String outfn;
	LabelManager lm;
	IDManager im;
	
	static int errSerial = 0;
	

	public ComplexQueryGenerator(String elfn, String initfn, String outfn) {
		this.elfn = elfn;
		this.initfn = initfn;
		this.outfn = outfn;
		lm = new SleepyCatLabelManager();
		im = new SleepyCatIDManager();
	}
	
	private QuerySchema getFullSchema(QueryGraph g) {
		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
		for (int j = g.nodeCount() - 1; j >= 0; j--)
			nset.add(g.getNode(j));
		return new QuerySchema(g, nset);
	}
	
	private boolean checkNotEmpty(QuerySchema sch) {
		try {
			if (logging) {
				QueryWriter wr = new FileQueryWriter("crash.log");
				
				wr.write(sch);
				wr.close();
			}
			
			Scan s = GraphStorage.queryPlanner.plan(sch).open();
			if (s.next()) {
				s.close();
				return true;
			}
			else {
				s.close();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logging) {
				QueryWriter wr = new FileQueryWriter("err" + (errSerial ++) + ".log");
				wr.write(sch);
				wr.close();
			}
			return false;
		}
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
					for (int j = sl.length - 1; j >= 0; j--) {
						
						avl.add(sl[j]);
					}
				}
				
				QueryGraph ng = GraphUtility.extendConstraint(qg, i, avl.get((int)Math.round(avl.size() * Math.random())));
				
				qarr.add(getFullSchema(ng));
			}
	}
	
	private void addEdge(QuerySchema qs, List<QuerySchema> qarr, List<String> elabels) {
		if (Math.random() > pee) return; 
		QueryGraph qg = qs.getQueryGraph();
		int nodeCount = qg.nodeCount(), labelCount = elabels.size(); 
		Set<QueryGraphNode> usedNode = new HashSet<QueryGraphNode>();
		
		for (int i = nodeCount - 1; i >= 0; i--) {
			
			System.out.println("  Extend edges from node " + i);
			int toExt;
			while (usedNode.contains(qg.getNode(toExt = (int)(Math.random() * nodeCount)))) ;
			usedNode.add(qg.getNode(toExt));
			
			Set<String>	testedLabel = new HashSet<String>();
			boolean found = false;
			
			int itr = 0;
			while (!found && itr < labelCount * propEdgeCheck) {
				itr ++;
				String el;
				
				while (testedLabel.contains(el = elabels.get((int)(labelCount * Math.random())))) ;
				testedLabel.add(el);
				
				boolean dir = (Math.random() > 0.5);
				
				System.out.println("    Querying [+Edge :: " + el + "] ...");
				QuerySchema nqs = getFullSchema(GraphUtility.extendEdge(qg, toExt, el, dir));
				found = checkNotEmpty(nqs);
				
				if (!found) {
					nqs = getFullSchema(GraphUtility.extendEdge(qg, toExt, el, dir));
					
					found = checkNotEmpty(nqs);
					if (found) qarr.add(nqs);
				}
				else
					qarr.add(nqs);
				
				System.out.println("    Query ended");
				
				testedLabel.add(el);
			}
		}
	}

	public void generate(int threshold) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			QueryWriter wr = new FileQueryWriter(outfn);
			QueryReader qp = new FileQueryReader(initfn);
			
			ArrayList<String> elabels = new ArrayList<String>();
			ArrayList<QuerySchema> qarr = new ArrayList<QuerySchema>();
			String temp;
			
			while ((temp = rd.readLine()) != null)
				elabels.add(temp);
			
			QuerySchema qs;
			
			while ((qs = qp.read()) != null)
				qarr.add(qs);
			
			int head = 0, orgsize = qarr.size();
			
			while (head < qarr.size() && (qarr.size() - orgsize < threshold)) {
				qs = qarr.get(head);
				
				int pp = qarr.size();
				System.out.println("CHECKING nodeid = " + head);
				addEdge(qs, qarr, elabels);
				constraintExtend(qs, qarr);
				System.out.println((qarr.size() - orgsize) + "ADDED");
				
				if (qarr.size() > pp)
					for (int i = pp; i < qarr.size(); i++)
						wr.write(qarr.get(i));
				
				head ++;
			}
			
			File f = new File("crash.log");
			f.delete();
			
			qp.close();
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ComplexQueryGenerator qg = new ComplexQueryGenerator(args[0], args[1], args[2]);
		
		qg.generate(Integer.parseInt(args[3]));
	}

}
