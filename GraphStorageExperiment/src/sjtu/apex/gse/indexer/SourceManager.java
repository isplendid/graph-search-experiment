package sjtu.apex.gse.indexer;

public interface SourceManager {
	
    /**
     * Initialize the index storage, to create the necessary tables
     */
    public void install();
    
    /** 
     * Return the uri of the source given the id
     * @param id - The id of the source
     * @return The uri of the source
     */
    public String getSource(int id);
    
    /**
     * Return the id of the source given the uri
     * @param source - The uri of the source
     * @return The id of the resource. If the URI does not exist in the dictionary, return -1.
     */
    public int getID(String source);
    
    /**
     * Add the source if it does not exist and return the id of the source given the uri
     * @param source - The uri of the source
     * @return The id of the resource.
     */
    public int addGetID(String source);
    
    /**
     * Add a new uri using a subsequent id
     * @param uri - The uri to the source to be added
     * @return The id of the new uri
     */
    public int addSource(String uri);
    
    /**
     * Release all resources occupied by this manager
     */
    public void close();

}
