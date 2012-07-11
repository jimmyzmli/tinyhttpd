
package jserver.config;

import  jserver.exception.*;

import java.io.*;
/**
 *
 * @author Jimmy
 */
public class HandlerExtendDir {
    public static void invoke( Configeration config, String paramStr ){
        //Make sure no whitespace formatings is kept.
        paramStr = paramStr.trim();
        //Find the proper parts.
        String[] data = paramStr.split(" ");
        //Get the handler information (ClassPath and port to handle for) from the given parameters.
        //Make sure the right amount of information is given.
        if( data.length == 2 ){
            File handlerFile = new File( data[1] );
            //Add the handler file only if it exists.
            if( ! handlerFile.exists() )
                throw new InvalidSettingException("The given handler file ( "+handlerFile.getAbsolutePath()+" ) is invalid...");
            else
                config.addElement( "handlerType", data[0]+"="+data[1] );
        }
        else
            //The wrong information, notify the user.
            throw new InvalidSettingException("Invaid format at ("+paramStr+"). The (handle port) then the (handler file) was expected.");
    }
}
