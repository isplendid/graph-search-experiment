package sjtu.apex.gse.system;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.metadata.PatternManager;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.planner.Planner;
import sjtu.apex.gse.planner.dp.DynamicProgrammingPlanner;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;


public class QuerySystem {
	private Configuration config;
	private PatternManager patternMan;
	private PatternCodec codec;
	private IndexManager indexMan;
	private ColumnNodeMap columnNodeMap;
	private Planner queryPlanner;
	private String folder;
	private int pl, psl;
	
	public QuerySystem(Configuration config) {
		this.config = config;
		codec = new HashingPatternCodec(new ModHash(config));
		
		folder = config.getStringSetting("DataFolder", null);
		pl = config.getIntegerSetting("PatternLength", 0);
		psl = config.getIntegerSetting("PatternStrSize", 128);
		
		indexMan = new IndexManager(folder, pl, psl);
		patternMan = new PatternManager(codec, indexMan);
		columnNodeMap = new HashLexicoColumnNodeMap(config);
		queryPlanner = new DynamicProgrammingPlanner(this);
	}
	
	public PatternManager patternManager() {
		return patternMan;
	}
	
	public PatternCodec patternCodec() {
		return codec;
	}
	
	public IndexManager indexManager() {
		return indexMan;
	}
	
	public ColumnNodeMap columnNodeMap() {
		return columnNodeMap;
	}
	
	public Planner queryPlanner() {
		return queryPlanner;
	}
	
	public Configuration config() {
		return config;
	}
	
	public String workingDirectory() {
		return folder;
	}
	
	public int patternSize() {
		return pl;
	}
	
	public int patternStrSize() {
		return psl;
	}
	
}
