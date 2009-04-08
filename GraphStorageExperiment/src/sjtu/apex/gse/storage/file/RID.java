
package sjtu.apex.gse.storage.file;

/**
 * 
 * @author Tian Yuan
 * 
 */
public class RID {
	private int offset;
	private int page;
	
	public RID(int page, int offset) {
		this.page = page;
		this.offset = offset;
	}
	
	public int getPageID() {
		return page;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		return "[" + page + " , " + offset + "]";
	}
}
