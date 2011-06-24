package sjtu.apex.gse.exp.sgm.ext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.index.file.FileIndexManager;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.pattern.DirectPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;

public class ExternalSubgraphMining {
	
	private PatternCodec codec = null; 
	private IndexManager idxman = null;
	private Configuration config = null;
	private IDManager idman = null;
	private double prop = 0;
	private double freq = 0;
	private int minEdge = 0;
	
	public ExternalSubgraphMining(String configFile, double freq, double prop, int minEdge) {
		config = new FileConfig(configFile);
		idxman = new FileIndexManager(config);
		codec = new DirectPatternCodec();
		idman = new SleepyCatIDManager(config);
		this.prop = prop;
		this.freq = freq;
		this.minEdge = minEdge;
	}
		
	private void traverseSubfolders(String root, Collection<File> files) {
		for (File f : new File(root).listFiles()) 
			if (f.isFile() && !f.getName().endsWith(".sparql"))
				files.add(f);
			else if (f.isDirectory())
				traverseSubfolders(f.getAbsolutePath(), files);
	}
	
	private boolean existsPattern(int sub, int pred, int obj) {
		QueryGraph g = new QueryGraph();
		
		QueryGraphNode subn = sub < 0 ? g.addNode() : g.addNode(sub);
		QueryGraphNode objn = obj < 0 ? g.addNode() : g.addNode(obj);
		g.addEdge(subn, objn, pred);
		
		String pattern = codec.encodePattern(g);
		
		return (idxman.seek(pattern, 2) != null);
	}
	
	private void convertInput(WordDictionary wd, String srcPath, String dest) throws IOException {
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
		
	}
	
	private void convertOutput(WordDictionary wd, String dest) throws IOException {
		String temp;
		File outf = new File(dest + ".fp");
		
		BufferedReader rd = new BufferedReader(new FileReader(outf));
		BufferedWriter wr = new BufferedWriter(new FileWriter(dest + ".out"));
		
		while ((temp = rd.readLine()) != null && temp.length() != 0) {
			Map<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
			StringBuilder sb = new StringBuilder();
			int tfreq = Integer.parseInt(temp.split(" ")[4]);
			int ec = 0, nc = 0;
			
			QueryGraph g = new QueryGraph();
			
			while ((temp = rd.readLine()) != null && temp.length() != 0) {
				String[] ts = temp.split(" ");
				
				if (ts.length == 3) {
					nc ++;
					if (Integer.parseInt(ts[1]) != (nc - 1))
						System.out.println("error");
					int hc = Integer.parseInt(ts[2]);
					if (hc == 0) {
						labelMap.put(nc - 1, -1);
						sb.append("*\n");
					}
					else {
						labelMap.put(nc - 1, idman.getID(wd.getWord(hc)));
						sb.append(wd.getWord(hc) + "\n");
					}
				}
				else {
					ec ++;
					
					int n1l = labelMap.get(Integer.parseInt(ts[1]));
					int n2l = labelMap.get(Integer.parseInt(ts[2]));
					
					int edgeLabel = idman.getID(wd.getWord(Integer.parseInt(ts[3])));
					
					if (existsPattern(n1l, n2l, edgeLabel))
						sb.append((Integer.parseInt(ts[1]) + 1) + " " + (Integer.parseInt(ts[2]) + 1) + " " + wd.getWord(Integer.parseInt(ts[3])) + "\n");
					else
						sb.append((Integer.parseInt(ts[2]) + 1) + " " + (Integer.parseInt(ts[1]) + 1) + " " + wd.getWord(Integer.parseInt(ts[3])) + "\n");					
				}
			}
			if (ec >= minEdge) {
				wr.append(nc + " " + ec + "\n");
				wr.append(sb.toString());
			}
		}
		
		wr.close();
		rd.close();
	
	}
	
	private void invokeExternal(String exec, String dest) throws IOException, InterruptedException {
		String[] cmd = new String[3];
		cmd[0] = "sh";
		cmd[1] = "-c";
		cmd[2] = exec + " -f " + dest + " " + " -s" + freq + " -o";
		
		Process p = Runtime.getRuntime().exec(cmd);
		int exitCode = p.waitFor();
	}
	
	public void mine(String srcPath, String dest, String exec) throws IOException, InterruptedException {
		WordDictionary wd = new WordDictionary();
		
		convertInput(wd, srcPath, dest);
		invokeExternal(exec, dest);
		convertOutput(wd, dest);
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		if (args.length < 7) {
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
		
		new ExternalSubgraphMining(args[0], Double.parseDouble(args[4]), Double.parseDouble(args[5]), minEdge).mine(args[1], args[2], args[3]);
	}

}