package sjtu.apex.gse.indexer;



/**
 * This interface manages functions regarding to ID
 * @author Tian Yuan
 * 
 */
public interface IDManager {
    
    /**
     * Initialize the index storage, to create the necessary tables
     */
    public void install();
    
    /** 
     * Return the uri of the resource given the id
     * @param id - The id of the resource
     * @return the uri of the resource
     */
    public String getURI(int id);
    
    /**
     * Return the id of the resource given the uri
     * @param uri - The uri of the resource
     * @return The id of the resource. If the URI does not exist in the dictionary, return -1.
     */
    public int getID(String uri);
    
    /**
     * Add a new uri using a subsequent id
     * @param uri - The uri to be added
     * @return The id of the new uri
     */
    public int addURI(String uri);
    
    /**
     * Release all resources occupied by this manager
     */
    public void close();

}