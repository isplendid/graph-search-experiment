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
		recLen = 4 * (size + 4);
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
			heapFile.close();
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
		int pointer = 0;
		int startPage, startOffset, endPage, endOffset;
		
		pointer = offset + recLen - 16;
		startPage = ((0x00FF & buf[pointer]) << 24) | ((0x00FF & buf[pointer + 1]) << 16) + ((0x00FF & buf[pointer + 2]) << 8) + (0x00FF & buf[pointer + 3]);
		pointer += 4;
		startOffset = ((0x00FF & buf[pointer]) << 24) | ((0x00FF & buf[pointer + 1]) << 16) + ((0x00FF & buf[pointer + 2]) << 8) + (0x00FF & buf[pointer + 3]);
		
		pointer += 4;
		endPage = ((0x00FF & buf[pointer]) << 24) | ((0x00FF & buf[pointer + 1]) << 16) + ((0x00FF & buf[pointer + 2]) << 8) + (0x00FF & buf[pointer + 3]);
		pointer += 4;
		endOffset = ((0x00FF & buf[pointer]) << 24) | ((0x00FF & buf[pointer + 1]) << 16) + ((0x00FF & buf[pointer + 2]) << 8) + (0x00FF & buf[pointer + 3]);
		
		return heapFile.getSourceSet(startPage, startOffset, endPage, endOffset);
	}

}
