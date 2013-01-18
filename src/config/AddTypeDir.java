
package jserver.config;

import jserver.*;
import jserver.exception.*;
import jserver.io.*;

/**
 *
 * @author Jimmy
 */
public class AddTypeDir {
    public static void invoke( Configeration config, String paramStr ){
        //Make sure no whitespace exsists.
        paramStr = paramStr.trim();
        
        String[] ele = paramStr.split(" ");

        if( ele.length > 1 ){
            String type = ele[0];
            //Add the type for each of the extensions given.
            for( int i=1; i<ele.length; i++ )
                config.addDataType( ele[i] , type );
        }
    }
}
