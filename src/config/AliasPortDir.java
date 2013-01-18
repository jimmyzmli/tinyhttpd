
package jserver.config;

import jserver.*;
import jserver.exception.*;
/**
 *
 * @author Jimmy
 */
public class AliasPortDir {
    public static void invoke( Configeration config, String paramStr ){
        //Parameter given is as follows:
        //Directive port-to-alias [alias-port(s)]
        paramStr = paramStr.trim();
        //Aquire data...
        int portStrEnd = paramStr.indexOf(" ");
        int port = parseInt( paramStr.substring( 0, portStrEnd ) );
        //Now get the aliaser ports.
        String[] aliasList = paramStr.substring( portStrEnd+1 ).trim().split(" ");

        for( String aliasPort : aliasList ){
            int alias = parseInt(aliasPort);
            ConnectionType.addAlias( port, alias );
            config.listenPorts.add(alias);
        }
        
    }

    private static int parseInt( String str ){
        try{
             return Integer.parseInt( str );
        }catch( NumberFormatException e ){
            throw new InvalidSettingException( "Integer Formating error, the given text does not translate to a valid port : "+e.getMessage() );
        }
    }
}
