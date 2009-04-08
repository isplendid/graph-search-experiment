package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.parser.FileQueryParser;
import sjtu.apex.gse.parser.QueryParser;
import sjtu.apex.gse.struct.QuerySchema;

/**
 * ComplexQueryGenerator generates complex queries with more edges and nodes
 * 
 * @author Tian Yuan
 *
 */
public class ComplexQueryGenerator {
	final double pec = 0.5;
	final double pee = 0.3;
	
	String elfn;
	String initfn;

	public ComplexQueryGenerator(String elfn, String initfn) {
		this.elfn = elfn;
		this.initfn = initfn;
	}
	
	private QuerySchema constraintExtend(QuerySchema qs) {
		
	}
	
	private QuerySchema addEdge(QuerySchema qs) {
		
	}

	public void generate(int threshold) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			QueryParser qp = new FileQueryParser(initfn);
			LabelManager lm = new SleepyCatLabelManager();
			
			ArrayList<String> elabels = new ArrayList<String>();
			ArrayList<QuerySchema> initQueries = new ArrayList<QuerySchema>();
			String temp;
			
			while ((temp = rd.readLine()) != null)
				elabels.add(temp);
			
			QuerySchema qs;
			
			while ((qs = qp.getNext()) != null)
				initQueries.add(qs);
			
			int head = 0, tail = initQueries.size();
			
			while (head < tail && tail < threshold) {
				qs = initQueries.get(head);
				
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
