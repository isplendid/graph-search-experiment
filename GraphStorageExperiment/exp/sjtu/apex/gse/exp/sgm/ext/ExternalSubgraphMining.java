package sjtu.apex.gse.exp.sgm.ext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExternalSubgraphMining {
		
	public static void traverseSubfolders(String root, Collection<File> files) {
		for (File f : new File(root).listFiles()) 
			if (f.isFile() && !f.getName().endsWith(".sparql"))
				files.add(f);
			else if (f.isDirectory())
				traverseSubfolders(f.getAbsolutePath(), files);
	}
	
	public static void mine(String srcPath, String dest, String exec, double freq, double prop, int minEdge) throws IOException, InterruptedException {
		WordDictionary wd = new WordDictionary();
		BufferedWriter wr = new BufferedWriter(new FileWriter(dest));
		List<File> files = new ArrayList<File>();
		traverseSubfolders(srcPath, files);
		String temp;
		int cnt = 0;

		for (File q : files) {
			BufferedReader rd = new BufferedReader(new FileReader(q));
			
			while ((temp = rd.readLine()) != null) {
				boolean samp = (Math.random() < prop);
				

				if (samp) wr.append("t # " + (cnt ++) + "\n");
				String[] t = temp.split("[ \t]+");
				int nc = Integer.parseInt(t[0]), ec = Integer.parseInt(t[1]);

				for (int i = 0; i < nc; i++) {
					temp = rd.readLine();

					if (samp) {
						if (temp.equals("*")) 
							wr.append("v " + i + " 0\n");
						else
							wr.append("v " + i + " " + wd.getID(temp) + "\n");
					}
				}

				for (int i = 0; i < ec; i++) {
					t = rd.readLine().split("[ \t]+");

					if (samp)
						wr.append("e " + (Integer.parseInt(t[0]) - 1) + " " + (Integer.parseInt(t[1]) - 1) + " "
								+ wd.getID(t[2]) + "\n");
				}

			}
			rd.close();
		}

		wr.close();
		
		String[] cmd = new String[3];
		cmd[0] = "sh";
		cmd[1] = "-c";
		cmd[2] = exec + " -f " + dest + " " + " -s" + freq + " -o";
		
		Process p = Runtime.getRuntime().exec(cmd);
		int exitCode = p.waitFor();
		
		File outf = new File(dest + ".fp");
		
		BufferedReader rd = new BufferedReader(new FileReader(outf));
		wr = new BufferedWriter(new FileWriter(dest + ".out"));
		
		while ((temp = rd.readLine()) != null && temp.length() != 0) {
			StringBuffer sb = new StringBuffer();
			int tfreq = Integer.parseInt(temp.split(" ")[4]);
			int ec = 0, nc = 0;
			while ((temp = rd.readLine()) != null && temp.length() != 0) {
				String[] ts = temp.split(" ");
				
				if (ts.length == 3) {
					nc ++;
					if (Integer.parseInt(ts[1]) != (nc - 1))
						System.out.println("error");
					int hc = Integer.parseInt(ts[2]);
					if (hc == 0)
						sb.append("*\n");
					else
						sb.append(wd.getWord(hc) + "\n");
				}
				else {
					ec ++;
					sb.append((Integer.parseInt(ts[1]) + 1) + " " + (Integer.parseInt(ts[2]) + 1) + " " + wd.getWord(Integer.parseInt(ts[3])) + "\n");
				}
			}
			if (ec >= minEdge) {
				wr.append(nc + " " + ec + "\n");
				wr.append(sb.toString());
			}
		}
		
		wr.close();
		rd.close();
//		outf.delete();
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		if (args.length < 6) {
			System.out.println("1. Configuration filename");
			System.out.println("2. Search root");
			System.out.println("3. Output filename");
			System.out.println("4. Executable for the gSpan");
			System.out.println("5. Frequency threshold");
			System.out.println("6. Probability of being selected");
			System.out.println("7. Mininum number of edges to be output (optional)");
		}
		
		int minEdge = 2;
		if (args.length >= 7) minEdge = Integer.parseInt(args[6]);
		
		mine(args[1], args[2], args[3], Double.parseDouble(args[4]), Double.parseDouble(args[5]), minEdge);
		
	}

}