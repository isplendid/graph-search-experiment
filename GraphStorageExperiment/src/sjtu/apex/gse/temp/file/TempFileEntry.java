package sjtu.apex.gse.temp.file;

import sjtu.apex.gse.storage.file.SourceHeapRange;

public class TempFileEntry {
	public String pattern;
	public int[] ins;
	public SourceHeapRange src;
	
	public TempFileEntry(String pattern, int[] ins, SourceHeapRange src) {
		this.pattern = pattern;
		this.ins = ins;
		this.src = src;
	}
}
