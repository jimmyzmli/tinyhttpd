
package jserver.config;

import jserver.*;

import java.util.*; //For data containers.

public class Configeration {
    /* A data class that stores the data loaded from the configeration file. No accessors will be used
     * because data should not be changed often.*/

    public List<Integer> listenPorts = new LinkedList<Integer>();

    public String rootDir = "./";
    public String defaultPage; //The default display page to look for in a directory.
    public String errPage;
    public String errMsg;
    
    private Map<String,String> dataTypes = new HashMap<String,String>(); //Extension=>HTML DataType translation table.
    //A table for extra elements that the server does not reconize imidiatly (Belonging to a addon or such)
    private Map<String, String> elementList = new HashMap<String,String>();

    //Some static default values:
    

    //Acess processors to the data. (A layer that transforms the raw data)
    public String getDataType( String ext ){
        /* Looks up the HTML data type associated with the given extension */
        return dataTypes.get(ext);
    }
    public void addDataType( String ext, String typeName){
        /* For implementation hiding of the translation tables */
        dataTypes.put( ext, typeName );
    }
    public String getElement( String key ){
        return elementList.get( key );
    }
    public void replaceElement( String key, String value ){
        //Simply replaces the value.
        elementList.put( key, value );
    }
    public void addElement( String key, String value ){
        // If the value is already set, then append to the value.
        String oldVal = getElement(key);
        if( oldVal != null )
            value = oldVal + Const.CONFIG_ELE_SEPERATOR + value;
        //Now actually do the replacing.
        elementList.put( key, value );
    }
    

    public Configeration(){
        /* The configeration object is a form object, meaning that the fields can be set and changed as
         * the user wishes... */
        //Update the global handle to the most recent config. (Meaning this)
        current = this;
    }

    //A global handle of the current config.
    private static Configeration current;
    //Accessors to the global handle.
    //An interface that returns the current instance in use, meaning the most recently created, or set.
    public static Configeration current(){
        return current;
    }
    public static void current( Configeration cur ){
        current = cur;
    }
}
