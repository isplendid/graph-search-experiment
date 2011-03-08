package sjtu.apex.gse.storage.file;

public class FileRepositoryEntry {
	public int[] bindings;
	public SourceHeapRange sourceHeapRange;
	
	public FileRepositoryEntry(int[] bindings, SourceHeapRange shr) {
		this.bindings = bindings;
		this.sourceHeapRange = shr;
	}
}
