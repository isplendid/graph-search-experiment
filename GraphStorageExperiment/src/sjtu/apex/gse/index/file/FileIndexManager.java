package sjtu.apex.gse.index.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.storage.file.RID;
import sjtu.apex.gse.storage.file.RecordRange;
import sjtu.apex.gse.storage.file.SourceHeapRange;
import sjtu.apex.gse.storage.file.SourceHeapReader;

public class FileIndexManager implements IndexManager {

	static final int lenSize = 2, intSize = 4;
	
	private RandomAccessFile file[];
	private String folder;
	private int recLen;
	private int strSize;
	private SourceHeapReader[] reader;
	
	public FileIndexManager(Configuration config) {
		this(config.getStringSetting("DataFolder", null), config.getIntegerSetting("PatternLength", 0), config.getIntegerSetting("PatternStrSize", 128));
	}
	
	public FileIndexManager(String folder, int size, int strSize) {
		this.folder = folder;
		this.strSize = strSize;
		
		reader = new SourceHeapReader[size];
		for (int i = 0; i < size; i++)
			reader[i] = new SourceHeapReader(folder + "/srcheap" + i);
		
		file = new RandomAccessFile[size];
		recLen = lenSize + strSize + intSize * 8;
		for (int i = 0; i < size; i++)
			try {
				file[i] = new RandomAccessFile(folder + "/index" + i, "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	private FileIndexEntry seekIndexEntry(String pattern, int size) {
		//Reopen file when seek after close() is invoked
		if (file[size - 1] == null) {
			try {
				file[size - 1] = new RandomAccessFile(folder + "/index" + (size - 1), "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		RandomAccessFile currentFile = file[size - 1];
		long pos, mid, head, tail;
		short strLen;
		byte buf[] = new byte[strSize];
		
		try {
			head = 0;
			tail = currentFile.length() / recLen - 1;
			
			while (head <= tail) {
				mid = (head + tail) / 2;
				pos = mid * recLen;
				currentFile.seek(pos);
				strLen = currentFile.readShort();
				currentFile.read(buf, 0, strLen);
				String str = new String(buf, 0, strLen);
				
				int cmp = pattern.compareTo(str);
				
				if (cmp < 0)
					tail = mid - 1;
				else if (cmp > 0)
					head = mid + 1;
				else {
					currentFile.seek(pos + lenSize + strSize);
					RecordRange rr;
					SourceHeapRange shr;
					int spageID, soffset, epageID, eoffset;
					
					spageID = currentFile.readInt();
					soffset = currentFile.readInt();
					epageID = currentFile.readInt();
					eoffset = currentFile.readInt();
					
					rr = new RecordRange(new RID(spageID, soffset), new RID(epageID, eoffset));
					
					spageID = currentFile.readInt();
					soffset = currentFile.readInt();
					epageID = currentFile.readInt();
					eoffset = currentFile.readInt();
					
					shr = new SourceHeapRange(new RID(spageID, soffset), new RID(epageID, eoffset));
					
					return new FileIndexEntry(pattern, rr, shr);
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public RecordRange seek(String pattern, int size) {
		FileIndexEntry fie;
		RecordRange ret;
		
		if ((fie = seekIndexEntry(pattern, size)) != null)
			ret = fie.range;
		else
			ret = null;
		
		return ret;
	}
	
	@Override
	public int getPatternCount(String pattern, int size) {
		if (size > file.length) return -1;
		RecordRange rr = seek(pattern, size);
		if (rr == null)
			return -1;
		long startpos = ((long)rr.getStartRID().getPageID() * 4096) + rr.getStartRID().getOffset();
		long endpos = ((long)rr.getEndRID().getPageID() * 4096) + rr.getEndRID().getOffset();
		
		return (int)((endpos - startpos)/(4 * size)) + 1;
	}
	
	@Override
	public void close() {
		try {
			for (int i = 0; i < file.length; i++) 
				if (file[i] != null){
					file[i].close();
					file[i] = null;
				}
			
			for (int i = 0; i < reader.length; i++)
				if (reader[i] != null) {
					reader[i].close();
					reader[i] = null;
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<Integer> getSourceList(String pattern, int size) {
		FileIndexEntry fie = seekIndexEntry(pattern, size);
		
		if (fie != null)
			return reader[size - 1].getSourceSet(fie.shr);
		else
			return null; 
	}
}
