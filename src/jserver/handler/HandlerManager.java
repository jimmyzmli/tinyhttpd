
package jserver.handler;

import jserver.*;
import jserver.config.*;
import jserver.exception.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 *
 * @author Jimmy
 */
public class HandlerManager {

    private static Map<Integer,Class> list = new HashMap<Integer,Class>();
    private static HandlerLoader loader = new HandlerLoader();

    static{
        loadAll(Configeration.current());
    }

    public static void loadAll( Configeration config ){
        //Loads all the handlers specified in the configeration.
        String dataStr = config.getElement("handlerType");

        if( dataStr == null ) return;
        String[] dataList = dataStr.split( Const.CONFIG_ELE_SEPERATOR+"" );

        for( String data : dataList ){
            String[] ele = data.split("=");
            if( ele.length == 2 )
                list.put( new Integer(ele[0]), loader.loadFrom(new File(ele[1])) );
        }
    }

    public static boolean hasPort( int port ){
        return ( list.get(port) != null );
    }
    public static InputHandler forPort( int port, Connection cnt ){
        
        Constructor factory = list.get(port).getConstructors()[0];
        try{
            return (InputHandler)factory.newInstance( cnt );
        }catch(Exception e){
            //If creation failed, then remove the handler from the list.
            list.put(port, null);
            //Notify the user.
            throw new SimpleMessage( "Failed to create the input handler "+factory.getName()+" : "+e.getMessage() );
        }
    }
    public static void handleFor( int port, Connection cnt ){
        try{
            Method processor = list.get(port).getDeclaredMethod( "handle", java.net.Socket.class );
            processor.invoke( null , cnt.getLink() );
        }catch(NoSuchMethodException e){
            //The processor can not be found, thus, remove it from the listing and notify.
            remove( port );
            throw new SimpleMessage( "Failed to locate the proper method in handler : "+e.getMessage() );
        }catch(Exception e){
            remove( port );
            throw new SimpleMessage( "ERROR in handling: "+e.getCause().getMessage() );
        }
    }
    public static void remove( int port ){
        list.put( port , null );
    }
}