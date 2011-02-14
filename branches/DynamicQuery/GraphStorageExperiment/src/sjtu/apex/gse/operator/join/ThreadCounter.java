package sjtu.apex.gse.operator.join;

public class ThreadCounter {
	private int endedThreadCount = 0;
	
	public synchronized int getEndedThreadCount() {
		return this.endedThreadCount;
	}
	
	public synchronized void threadEnded() {
		this.endedThreadCount ++;
	}
}