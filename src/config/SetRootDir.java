
package jserver.config;

import jserver.exception.*;

import java.io.*;

/**
 *
 * @author Jimmy
 */
public class SetRootDir {
    public static void invoke( Configeration config, String paramStr ){
        //First, see if the string is quoted and rid of all spaces.
        paramStr = paramStr.trim();
        paramStr = paramStr.replaceAll( "\"" , "" );
        paramStr = paramStr.replaceAll( "'" , "" );
        //Now, check to see if the given is a valid directory
        File rootPath = new File( paramStr );
        if( ! rootPath.exists() || rootPath.isFile() )
            throw new InvalidSettingException("The given root directory is not valid.");

        //Find the exact path.
        try{
            config.rootDir = rootPath.getCanonicalPath().concat("/");
        }catch( IOException e ){
            //Fails to find the exact path?
            throw new InvalidSettingException("Failed to find the given root directory.");
        }
  
    }
}
