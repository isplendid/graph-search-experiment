package sjtu.apex.gse.experiment;

import java.io.File;
import java.io.IOException;

public class BatchExperiment {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f = new File(args[1]);
		File[] queries = f.listFiles();

		for (File q : queries) {
			String[] pa = new String[3];
			pa[0] = args[0];
			pa[1] = q.getAbsolutePath();
			pa[2] = args[1] + "\\" + q.getName();
			Experiment.main(pa);
		}
	}

}
