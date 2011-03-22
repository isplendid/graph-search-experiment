package sjtu.apex.gse.experiment.querygen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.experiment.edge.Edge;
import sjtu.apex.gse.experiment.edge.EdgeInfo;
import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.FileQueryWriter;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.QueryWriter;
import sjtu.apex.gse.struct.Connectivity;
import sjtu.apex.gse.struct.GraphUtility;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;
import sjtu.apex.gse.system.QuerySystem.QuerySystemMode;

/**
 * ComplexQueryGenerator generates complex queries with more edges and nodes
 * 
 * @author Tian Yuan
 *
 */
public class ComplexQueryGenerator {
	
	final static int extMin = 2;
	
	double pec = 0.01;
	double pee1 = 0.5;
	double pee2 = 0.03;
	int edgeScanMax = 100;
	int conScanMax = 3000;
	
	String eip;
	LabelManager lm;
	IDManager im;
	EdgeInfo einfo;
	QuerySystem sys;
	
	static int errSerial = 0;
	

	public ComplexQueryGenerator(Configuration config) {
		eip = config.getStringSetting("EdgeInfoFolder", null);
		pec = config.getDoubleSetting("ItrGenConExt", pec);
		pee1 = config.getDoubleSetting("ItrGenEdgeExt1", pee1);
		pee2 = config.getDoubleSetting("ItrGenEdgeExt2", pee2);
		edgeScanMax = config.getIntegerSetting("ItrGenConScan", edgeScanMax);
		conScanMax = config.getIntegerSetting("ItrGenEdgeScan", conScanMax);
		
		sys = new QuerySystem(config, QuerySystemMode.FILE_ONLY);
		lm = new SleepyCatLabelManager(config);
		im = sys.idManager();
		einfo = new EdgeInfo(eip);
	}
	
	private QuerySchema getFullSchema(QueryGraph g) {
		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
		for (int j = g.nodeCount() - 1; j >= 0; j--)
			nset.add(g.getNode(j));
		return new QuerySchema(g, nset);
	}
	
	private List<QuerySchema> constraintExtend(QuerySchema qs) {
		List<QuerySchema> ret = new ArrayList<QuerySchema>();
		
		QueryGraph qg = qs.getQueryGraph();
		int cnt = 0;
		
		Plan p = sys.queryPlanner().plan(qs);
		Scan s = p.open();
		while (s.next() && cnt < conScanMax) {
			cnt ++;
			for (int i = qg.nodeCount() - 1; i >= 0; i--)
				if (qg.getNode(i).isGeneral()) {
					boolean canBeExt = true;
					
					for (Connectivity c : qg.getNode(i).getConnectivities()) 
						if (!c.getNode().isGeneral()) {
							canBeExt = false;
							break;
						}
				
					if (canBeExt && Math.random() < pec) {
						QueryGraphNode extNode = qg.getNode(i);
						int ni = s.getID(extNode);
						
						QueryGraph ng = GraphUtility.extendConstraint(qg, i, ni);
	
						ret.add(getFullSchema(ng));
					}
			}
		}
		s.close();
		
		return ret;
	}
	
	private List<QuerySchema> edgeExtend(QuerySchema qs) {
		List<QuerySchema> ret = new ArrayList<QuerySchema>();
		
		if (Math.random() > pee1)
			return ret;
		
		Set<Integer> existEdgeLabel = new HashSet<Integer>();
		List<Set<Edge>> list = new ArrayList<Set<Edge>>();
		
		QueryGraph qg = qs.getQueryGraph();
		int nodeCount = qg.nodeCount();
		int cnt = 0;
		
		for (int i = qg.edgeCount() - 1; i >= 0; i--)
			existEdgeLabel.add(qg.getEdge(i).getLabel());
		
		for (int i = 0; i < nodeCount; i++)
			list.add(new HashSet<Edge>());
		
		Scan s = sys.queryPlanner().plan(qs).open();
		
		while (s.next() && cnt < edgeScanMax) {
			cnt ++;
			for (int i = 0; i < nodeCount; i++) {
				QueryGraphNode qn = qg.getNode(i);
				int nid = s.getID(qn);
				List<Edge> edges = einfo.getEdges(nid); 
				Set<Edge> set = list.get(i);
				
				for (Edge e : edges)
					if (!set.contains(e)) set.add(e);
			}
		}
		s.close();
		
		System.out.println("\tRandomly generating queries ...");
		for (int i = 0; i < nodeCount; i++) {
			
			for (Edge e : list.get(i))
				if (!existEdgeLabel.contains(e.getLabel()) && (ret.size() < extMin || Math.random() < pee2)) {
					System.out.println("\t" + e.toString() + " added to node " + qg.getNode(i) + ".");
					QuerySchema nqs = getFullSchema(GraphUtility.extendEdge(qg, i, e.getLabel(), e.getDir()));
					
					ret.add(nqs);
				}
			
		}
		
		return ret;
	}

	public void generate(String infldr, String outfldr, int threshold, int mod) {
		int outcnt = 0;
		ArrayList<QuerySchema> qarr = new ArrayList<QuerySchema>();
		
		for (String file : FilesystemUtility.listAllFiles(infldr)) {
			QueryReader qp = new FileQueryReader(file, sys.idManager());
			QuerySchema qs;
			
			while ((qs = qp.read()) != null)
				qarr.add(qs);
			qp.close();
		}
		
		int head = 0, orgsize = qarr.size();
		
		while (head < orgsize && head < qarr.size() && (qarr.size() - orgsize < threshold)) {
			QuerySchema qs = qarr.get(head);
			
			int pp = qarr.size();
			System.out.println("CHECKING nodeid = " + head);
			
			if ((mod & 0x00000001) != 0) qarr.addAll(edgeExtend(qs));
			if ((mod & 0x00000002) != 0) qarr.addAll(constraintExtend(qs));
			
			System.out.println((qarr.size() - orgsize) + "ADDED");
			
			if (qarr.size() > pp)
				for (int i = pp; i < qarr.size(); i++) {
					QueryWriter wr = new FileQueryWriter(outfldr + "/q." + outcnt , sys.idManager());
					wr.write(qarr.get(i));
					outcnt++;
					wr.close();
				}
			
			head ++;
		}
	}
	
	public void close() {
		lm.close();
		sys.close();
		einfo.close();
	}

	/**
	 * 
	 * @param args Configuration, Edge label filename, Initial queries, Output, threshold
	 */
	public static void main(String[] args) {
		Configuration cf = new FileConfig(args[0]); 
		ComplexQueryGenerator qg = new ComplexQueryGenerator(cf);

		for (String folder : FilesystemUtility.listAllFiles(args[1])) {
			String in = folder;
			String out = args[2] + "/" + FilesystemUtility.getBaseName(folder);
			
			FilesystemUtility.createDir(out);
			
			qg.generate(in, out, Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		}
		qg.close();
	}

}
