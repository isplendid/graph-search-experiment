package sjtu.apex.gse.operator;

public interface RestorableScan extends Scan {
	/**
	 * Save the current position of cursor
	 */
	public void savePosition();
	
	/**
	 * Restore the cursor to the previously saved position
	 */
	public void restorePosition();
}
