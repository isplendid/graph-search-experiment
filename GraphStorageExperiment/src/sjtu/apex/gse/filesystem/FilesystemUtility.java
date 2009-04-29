package sjtu.apex.gse.filesystem;

import java.io.File;

public class FilesystemUtility {
	
	public static void deleteFile(String filename) {
		File f = new File(filename);
		f.delete();
	}
	
	public static void renameFile(String src, String dest) {
		File f = new File(src);
		f.renameTo(new File(dest));
	}
	
	public static boolean fileExist(String filename) {
		File f = new File(filename);
		return f.exists();
	}
	
}
