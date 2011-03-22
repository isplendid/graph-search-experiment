package sjtu.apex.gse.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	public static void createDir(String dir) {
		File f = new File(dir);
		if (!f.exists()) f.mkdir();
	}

	public static void deleteDir(String dir) {
		deleteDirectory(new File(dir));
	}

	private static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i=0; i<files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return(path.delete());
	}

	public static List<String> listAllFiles(String path) {
		List<String> ret = new ArrayList<String>();
		
		File dir = new File(path);
		
		for (File file : dir.listFiles()) {
			ret.add(file.getAbsolutePath());
		}
		return ret;
	}
	
	public static String getBaseName(String path) {
		return new File(path).getName();
	}
}
