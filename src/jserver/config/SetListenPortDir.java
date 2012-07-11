
package jserver.config;

import jserver.*;
import jserver.exception.*;

/**
 *
 * @author Jimmy
 */
public class SetListenPortDir {
    public static void invoke( Configeration config, String paramStr ){
        //Adds the given port(s) to the listen list.
        String[] ports = paramStr.split(",");

        for( String port : ports )
            config.listenPorts.add( new Integer(port) );
    }
}
