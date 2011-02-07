package sjtu.apex.gse.storage.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.struct.QueryGraphNode;


/**
 * 
 * @author Tian Yuan
 * 
 */
public class FileRepository implements Scan {

	RandomAccessFile file;
	byte buf[] = new byte[4096];
	RecordRange range;
	int page = -1, offset = -1;
	int recLen;
	SourceHeapReader heapFile;

	public FileRepository(IndexManager indexMan, String filename, String pattern, int size, SourceHeapReader heap) {
		range = indexMan.seek(pattern, size);
		recLen = 4 * (size + 2);
		heapFile = heap;
		try {
			file = new RandomAccessFile(filename, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		beforeFirst();
	}


	@Override
	public void beforeFirst() {
		try {
			page = range.getStartRID().getPageID();
			offset = range.getStartRID().getOffset() - recLen;
			file.seek((long)page * 4096);
			file.read(buf, 0, 4096);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getID(QueryGraphNode n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getID(int nodeID) {
		int numPos = offset + 4 * nodeID;
		try {
			return ((0x00FF & buf[numPos]) << 24) | ((0x00FF & buf[numPos + 1]) << 16) + ((0x00FF & buf[numPos + 2]) << 8) + (0x00FF & buf[numPos + 3]);
		} catch (Exception e) {
			System.out.println(nodeID + " , " + numPos);
			return ((0x00FF & buf[numPos]) << 24) | ((0x00FF & buf[numPos + 1]) << 16) + ((0x00FF & buf[numPos + 2]) << 8) + (0x00FF & buf[numPos + 3]);
		}
	}

	@Override
	public boolean hasNode(QueryGraphNode n) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean next() {
		offset += recLen;
		if (page > range.getEndRID().getPageID() || page == range.getEndRID().getPageID() && offset > range.getEndRID().getOffset()) return false;
		if (offset + recLen > 4096) {
			page++;
			try {
				file.seek((long)page * 4096);
				file.read(buf, 0, 4096);
			} catch (Exception e) {
				e.printStackTrace();
			}
			offset = 0;
		}
		
		return true;
	}


	@Override
	public Set<Integer> getSourceSet() {
		int startPos, endPos;
		int startIdx, endIdx;
		
		startPos = offset + recLen - 8;
		endPos = offset + recLen - 4;
		startIdx = ((0x00FF & buf[startPos]) << 24) | ((0x00FF & buf[startPos + 1]) << 16) + ((0x00FF & buf[startPos + 2]) << 8) + (0x00FF & buf[startPos + 3]);
		endIdx = ((0x00FF & buf[endPos]) << 24) | ((0x00FF & buf[endPos + 1]) << 16) + ((0x00FF & buf[endPos + 2]) << 8) + (0x00FF & buf[endPos + 3]);
		
		return heapFile.getSourceSet(startIdx, endIdx);
	}

}
