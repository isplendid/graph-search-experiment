package sjtu.apex.gse.nt;


public class KwRelExtract {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NTReader rd = new NTReader(args[0]);
		NTWriter kw = new NTWriter(args[1]);
		NTWriter rel = new NTWriter(args[2]);
		StringBuffer sb = new StringBuffer();
		String lastsub = null;
		
		while (rd.next()) {
			Triple tt = rd.tri; 
			if (tt.o.startsWith("\"")) {
				
				if (lastsub != null && !lastsub.equals(tt.s)) {
					sb.append("\"");
					kw.write(new Triple(lastsub, "<http://sjtu.edu.cn/label>", sb.toString()));
					sb = new StringBuffer();
					sb.append("\"");
				}
				
				sb.append(tt.o.substring(tt.o.indexOf("\"") + 1, tt.o.lastIndexOf("\"")) + " ");
				lastsub = tt.s;
			}
			else
				rel.write(tt);
		}
		
		rd.close();
		kw.close();
		rel.close();
	}

}
