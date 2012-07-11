
package jserver;

import jserver.handler.*;
import jserver.request.*;

import java.util.*;
import java.lang.reflect.*;

public enum ConnectionType{
        
        //A list of currently known supported types.
        //The connection should specify the port, given request's type, and the handler class.
        ANY( 0, RequestType.UNKNOWN, UnknownInputHandler.class ),
        HTML( 80, RequestType.HTTP, HttpInputHandler.class )
        ;

        public static ConnectionType port( int portNum ){
            /* Use the port number given to find the connection type. */
            //Go thru each type and look for the number.
            for( ConnectionType type : values() )
                if( type.port == portNum )
                    return type;

            //If the port is not a known one, check to see if the port is a "transfer point". (An alias for another port)
            Integer port = getAlias( portNum );
            if( port != null )
                //Find the port type.
                return port(port);

            //No type with that port number found. Meaning that this is a unkown port.
            return ANY;
       }
        //A list of aliases. Or Transfer points.
        public static final Map<Integer,Integer> aliasList = new HashMap<Integer,Integer>();

        public static void addAlias( int port, int alias ){
            //Associate the real port with the transfer point.
            aliasList.put( alias, port );
        }
        public static Integer getAlias( int alias ){
            //Get whatever the transfer point "alias" is transfering to.
            return aliasList.get( alias );
        }
        //Properties are acessed directly.
        public final int port;
        public final RequestType requestType;
        private final Constructor factory;
        ConnectionType( int portNum , RequestType reqType, Class handlerCls ){
            //Assignment, sets the properties of this type.
            port = portNum;
            requestType = reqType;
            //Get the creation method ("Factory") of the handler.
            factory = handlerCls.getConstructors()[0];
        }

        public InputHandler createInputHandler( Connection cnt ){
            //Creates a handler for the given connection.
            try{
                //Generate a handler
                return (InputHandler)factory.newInstance( cnt );
            }catch(Exception e){
                //Cannot create the handler... MAJOR ERROR!
                Err.fatal("The handler for the request type "+toString()+" cannot be created! Please reinstall the server. (Bad files) : "+
                          e.getMessage());
            }
            //If the creation goes bad, indicate so.
            return null;
        }

}