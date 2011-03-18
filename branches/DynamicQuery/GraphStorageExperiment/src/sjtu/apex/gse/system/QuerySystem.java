package sjtu.apex.gse.system;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.SourceManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.indexer.file.SleepyCatSourceManager;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.metadata.PatternManager;
import sjtu.apex.gse.operator.factory.FileOperatorFactory;
import sjtu.apex.gse.operator.factory.OperatorFactory;
import sjtu.apex.gse.operator.factory.WebOperatorFactory;
import sjtu.apex.gse.pattern.DirectPatternCodec;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.planner.Planner;
import sjtu.apex.gse.planner.dp.DynamicProgrammingPlanner;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.storage.map.LexicoColumnNodeMap;


public class QuerySystem {
	private Configuration config;
	private PatternManager patternMan;
	private PatternCodec codec;
	private IndexManager indexMan;
	private ColumnNodeMap columnNodeMap;
	private Planner queryPlanner;
	private String folder;
	private int pl, psl;
	private QuerySystemMode sysMode;
	
	private SourceManager sourceMan;
	private ie.deri.urq.lidaq.repos.WebRepository webRepos;
	private IDManager idMan;
	
	public enum QuerySystemMode {
		WEB_ONLY, FILE_ONLY 
	}
	
	public QuerySystem(Configuration config) {
		this(config, QuerySystemMode.WEB_ONLY);
	}
	
	public QuerySystem(Configuration config, QuerySystemMode mode) {
		this.config = config;
		this.sysMode = mode;
		
		paramsInit();
		
		indexMan = new IndexManager(folder, pl, psl);
		
		
		patternManagerInit();
		queryPlannerInit(mode);
		
		
		sourceMan = new SleepyCatSourceManager(config);
		idMan = new SleepyCatIDManager(config);
		
		webReposInit();
	}
	
	private void webReposInit() {
		if (sysMode == QuerySystemMode.WEB_ONLY && config.getIntegerSetting("WebAccess", 0) > 0) {
			String host = config.getStringSetting("LDProxyHost", null);
			String port = config.getStringSetting("LDProxyPort", null);
			
			webRepos = new ie.deri.urq.lidaq.repos.WebRepository(host, port);
		}
	}
	
	private void paramsInit() {
		folder = config.getStringSetting("DataFolder", null);
		
		pl = config.getIntegerSetting("PatternLength", 0);
		psl = config.getIntegerSetting("PatternStrSize", 128);
	}
	
	private void patternManagerInit() {
		String pt = config.getStringSetting("PatternType", "Hash").toLowerCase();
		
		if (pt.equals("hash")) {
			HashFunction hf = new ModHash(config);
			codec = new HashingPatternCodec(hf);
			columnNodeMap = new HashLexicoColumnNodeMap(hf);
		} else if (pt.equals("direct")){
			codec = new DirectPatternCodec(); 
			columnNodeMap = new LexicoColumnNodeMap();
		}
		
		patternMan = new PatternManager(codec, indexMan);
	}
	
	private void queryPlannerInit(QuerySystemMode mode) {
		OperatorFactory opFac = null;
		
		switch (mode) {
			case WEB_ONLY:
				opFac = new WebOperatorFactory();
				break;
			case FILE_ONLY:
				opFac = new FileOperatorFactory();
				break;
		}
	
		queryPlanner = new DynamicProgrammingPlanner(this, opFac);
	}
	
	public ie.deri.urq.lidaq.repos.WebRepository webRepository() {
		return webRepos;
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
	
	public SourceManager sourceManager() {
		return sourceMan;
	}
	
	public IDManager idManager() {
		return idMan;
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
	
	public void close() {
		sourceMan.close();
		idMan.close();
		if (webRepos != null)
			webRepos.shutdown();
	}
	
}
