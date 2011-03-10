package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.FileQueryWriter;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.QueryWriter;
import sjtu.apex.gse.struct.QuerySchema;

public class ExpandSeedSelection {
	
	final static int threshold = 1000;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		Configuration conf = new FileConfig(args[3]);
		QueryReader rd = new FileQueryReader(args[0], new SleepyCatIDManager(conf));
		BufferedReader freq = new BufferedReader(new FileReader(args[1]));
		QueryWriter wr = new FileQueryWriter(args[2], new SleepyCatIDManager(conf));
		QuerySchema sch;
		
		while ((sch = rd.read()) != null) {
			int cnt = Integer.parseInt(freq.readLine().split("\t ")[1]);
			
			if (cnt < threshold)
				wr.write(sch);
		}
		
		wr.close();
		rd.close();
	}

}
