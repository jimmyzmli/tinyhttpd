
package jserver;

import jserver.exception.*;

import java.io.*;
import java.net.*;

public class Connection {
    /* The abstraction of an internet connection. This allows transfer of data and
     * requests across the "connection". */
    //The actual link to the client.
    Socket link;
    ConnectionType type;
    public Connection( Socket cnt, ConnectionType t ){
        //Assignment.
        link = cnt;
        type = t;
    }

    /**
     * Close the connection, this means that all transfer will be interrupted and streams
     * will be closed.
     */
    public void close(){
        //Close the linkage.
        try{
            link.close();
        }catch(IOException e){
            throw new BadConnectionException("Connection at port "+link.getPort()+" failed to close.");
        }
    }

    //Acessors.
    public Socket getLink(){ return link; }
    public InputStream getInputStream() throws IOException{
        return link.getInputStream();
    }
    public OutputStream getOutputStream() throws IOException{
        return link.getOutputStream();
    }
    public ConnectionType getType(){ return type; }
    public jserver.request.RequestType getRequestType(){
        //Get the type of request sent over this connection.
        return type.requestType;
    }

    
}
