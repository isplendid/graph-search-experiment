package sjtu.apex.gse.storage.file;

public class SourceHeapRange {
	long startIdx, endIdx;
	
	public SourceHeapRange(long startIdx, long endIdx) {
		this.startIdx = startIdx;
		this.endIdx = endIdx;
	}
	
	public long getStartIndex() {
		return startIdx;
	}
	
	public long getEndIndex() {
		return endIdx;
	}
}
