
package jserver.config;

import java.io.*;

/**
 * 
 * @author Jimmy
 */
public class LoadModuleDir {
    public static void invoke( Configeration config, String paramStr ){
        File modFile = new File( paramStr );
        if( modFile.exists() )
            config.addElement( "ext", paramStr );
    }
}
