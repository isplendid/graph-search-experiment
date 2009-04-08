package sjtu.apex.gse.temp.file.util;

import java.util.Comparator;

import sjtu.apex.gse.temp.file.TempFileEntry;


public class TempFileEntryComparator implements Comparator<TempFileEntry> {

	@Override
	public int compare(TempFileEntry o1, TempFileEntry o2) {
		return o1.pattern.compareTo(o2.pattern);
	}

}
