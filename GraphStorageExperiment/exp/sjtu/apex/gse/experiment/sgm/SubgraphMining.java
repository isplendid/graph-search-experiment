package sjtu.apex.gse.experiment.sgm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sjtu.apex.gse.hash.HashFunction;

public class SubgraphMining {
	
	public static void mine(HashFunction hf, String srcPath, String tmpPath) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(tmpPath + "/tmp"));
		File f = new File(srcPath);
		File[] qFile = f.listFiles();
		String temp;
		int cnt = 0;

		for (File q : qFile) {
			BufferedReader rd = new BufferedReader(new FileReader(q));
			
			while ((temp = rd.readLine()) != null) {
				wr.append("t # " + (cnt ++) + "\n");
				String[] t = temp.split("[ \t]+");
				int nc = Integer.parseInt(t[0]), ec = Integer.parseInt(t[1]);
				
				for (int i = 0; i < nc; i++) {
					temp = rd.readLine();
					
					if (temp.equals("*")) 
						wr.append("v " + i + " *\n");
					else
						wr.append("v " + i + " " + hf.hashStr(temp) + "\n");
				}
				
				for (int i = 0; i < ec; i++) {
					t = rd.readLine().split("[ \t]+");
					wr.append("e " + (Integer.parseInt(t[0]) - 1) + " " + (Integer.parseInt(t[1]) - 1) + " "
							+ t[2] + "\n");
				}
			}
			rd.close();
		}

		wr.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
