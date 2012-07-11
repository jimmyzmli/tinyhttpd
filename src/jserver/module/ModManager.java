
package jserver.module;

import jserver.config.Configeration;
import jserver.*;
import jserver.request.*;
import jserver.data.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class ModManager {
    /* Determines the type of modules to use to process the given data. Then uses that
     * processor. */

    private static List<Method> mods = new LinkedList<Method>();
    private static ModLoader loader = new ModLoader();

    static{
        //Loads all the modules on load.
        addMods( Configeration.current() );
    }

    private static Class loadMod( String path ){
        return loadMod( new File(path) );
    }
    private static Class loadMod( File path ){
        return loader.loadFrom( path );
    }
    private static Method getProcessingMethod( Class mod ){
        /* Gets the processing funcationality of the module class. */
        try{
            return mod.getMethod( "process", Request.class, Data.class );
        }catch(NoSuchMethodException e){
            Err.report("Error with a module. The module is not valid, please try to reinstall it.");
        }
        //Return indicating that the method does not exsit.
        return null;
    }

    public static void addMods( Configeration config ){
        /* Adds all the modules specified in the configeration into the mod list. */
        String pathStr = config.getElement("ext");
        if( pathStr == null ) return; //No modules to be added.
        String[] paths = pathStr.split( Const.CONFIG_ELE_SEPERATOR+"" );
        //Get the processing methods for each module.
        for( String path : paths ){
            Class mod = loadMod( path );
            //Make sure the module is loaded.
            if( mod == null )   continue;
            //Then get the method needed.
            //After making sure that the method is not blank.
            Method processor = getProcessingMethod( mod );
            if( processor != null)
                mods.add(processor);
            else
                Err.report("The module \""+mod.getName()+"\" is not valid. It does not have the methods required. ");
        }
    }

    public static void process( Request req, Data data ){
        //Go thru the mods that need to process the data.
        try{
            for( Method mod : mods ){
                //Call the module methods.
                mod.invoke( null, req, data);
            }
        }catch(InvocationTargetException e ){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
            Err.fatal("Something is wrong with a module's execution.");
        }
    }
}
