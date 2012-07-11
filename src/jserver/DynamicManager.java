
package jserver;

import jserver.config.Configeration;

import java.io.*;

public abstract class DynamicManager {
    /* Determines the type of modules to use to process the given data. Then uses that
     * processor. */

    public static void load( String path ){
        load( new File(path) );
    }
    public static void load( File path ){};

    public static void loadMods( Configeration config ){
        /* Adds all the modules specified in the configeration into the mod list. */
        String[] paths = readModulePaths( config );
        //Make sure the path exsists.
        if( paths == null ) return;
        //Get the processing methods for each module.
        for( String path : paths ){
            load( path );
        }
    }
    public static String[] readModulePaths( Configeration config ){ return null; }

}
