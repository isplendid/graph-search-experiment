package sjtu.apex.gse.storage.file;

/**
 * 
 * @author Tian Yuan
 * 
 */
public class RecordRange {
	private RID start, end;
	
	public RecordRange(RID start, RID end) {
		this.start = start;
		this.end = end;
	}
	
	public RID getStartRID() {
		return start;
	}
	
	public RID getEndRID() {
		return end;
	}
}
