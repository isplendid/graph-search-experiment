package sjtu.apex.gse.exp;

import java.io.File;
import java.io.IOException;

public class BatchExperiment {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String configFn = args[0];
		String queryFldr = args[1];
		String resultFldr = args[2];
		
		File f = new File(queryFldr);
		File[] queries = f.listFiles();

		for (File q : queries)
			if (q.isFile()){
				String[] pa = new String[3];
				pa[0] = configFn;
				pa[1] = q.getAbsolutePath();
				pa[2] = resultFldr + "/" + q.getName();
				System.out.println("Start to process query " + pa[1]);
				Experiment.main(pa);
			}
	}

}
