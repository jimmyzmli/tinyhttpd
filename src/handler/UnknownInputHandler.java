
package jserver.handler;

import jserver.util.ServerUtil;
import jserver.*;

import java.io.*;

public class UnknownInputHandler extends InputHandler{
    /* Handles all requests sent to an unregistered port. Basically the only way to handle
     * such a connection is to terminate it. */

    public UnknownInputHandler( Connection client ){ super( client ); }

    public boolean keepAlive(){ return false; }
    public String nextRequestString(){
        try{
            return ServerUtil.getData( cnt.getInputStream(), '\r' );
        }catch( IOException e ){
            Err.report( "Failed to read from client, an unknown request was sent to port "+cnt.getLink().getPort()+" : "+e.getMessage() );
        }
        //Give the request processor an empty line if the connection failed.
        return "";
    }

}
