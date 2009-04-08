package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.parser.FileQueryParser;
import sjtu.apex.gse.parser.QueryParser;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QuerySchema;

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

	public ComplexQueryGenerator(String elfn, String initfn) {
		this.elfn = elfn;
		this.initfn = initfn;
	}
	
	private void constraintExtend(QuerySchema qs, ArrayList<QuerySchema> qarr) {
		QueryGraph qg = qs.getQueryGraph();
		
		for (int i = qg.nodeCount() - 1; i >= 0; i--)
			if (qg.getNode(i).isGeneral() && Math.random() < pec) {
				
			}
	}
	
	private void addEdge(QuerySchema qs, ArrayList<QuerySchema> qarr) {
		
	}

	public void generate(int threshold) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			QueryParser qp = new FileQueryParser(initfn);
			LabelManager lm = new SleepyCatLabelManager();
			
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
				
				addEdge(qs, qarr);
				constraintExtend(qs, qarr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
