package sjtu.apex.gse.debug;

import sjtu.apex.gse.index.file.util.FileIndexMerger;

public class Bug20090505p1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileIndexMerger.merge(args[0], args[0], "index", "storage", 2, 128, 2);
	}

}
