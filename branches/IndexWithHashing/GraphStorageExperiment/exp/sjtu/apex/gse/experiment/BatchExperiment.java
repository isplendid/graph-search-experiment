package sjtu.apex.gse.experiment;

import java.io.File;
import java.io.IOException;

public class BatchExperiment {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f = new File(args[0]);
		File[] queries = f.listFiles();

		for (File q : queries) {
			String[] pa = new String[2];
			pa[0] = q.getAbsolutePath();
			pa[1] = args[1] + "\\" + q.getName();
			Experiment.main(pa);
		}
	}

}
