package sjtu.apex.gse.storage.file;

public class SourceHeapRange {
	RID startIdx, endIdx;
	
	public SourceHeapRange(RID startIdx, RID endIdx) {
		this.startIdx = startIdx;
		this.endIdx = endIdx;
	}
	
	public RID getStartIndex() {
		return startIdx;
	}
	
	public RID getEndIndex() {
		return endIdx;
	}
}
