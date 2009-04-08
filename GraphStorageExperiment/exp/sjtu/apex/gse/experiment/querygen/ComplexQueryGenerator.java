package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;

/**
 * ComplexQueryGenerator generates complex queries with more edges and nodes
 * 
 * @author Tian Yuan
 *
 */
public class ComplexQueryGenerator {
	String elfn;

	public ComplexQueryGenerator(String elfn) {
		this.elfn = elfn;
	}

	public void generate() {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			LabelManager lm = new SleepyCatLabelManager();
			
			ArrayList<String> elabels = new ArrayList<String>();
			String temp;
			
			while ((temp = rd.readLine()) != null)
				elabels.add(temp);
			
			
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
