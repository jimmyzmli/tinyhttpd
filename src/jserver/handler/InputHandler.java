
package jserver.handler;

import jserver.*;
import jserver.exception.UnknownRequestException;


public abstract class InputHandler {
    /* Handles the connection's input, and returns it as string segments (Providing also
     * a buffering functionality). All inputs will be processed before the connection is terminated. */

    //Static factory method.
    public static InputHandler createHandler( Connection cnt ){
        //Creates the proper handler for the given connection.
        return cnt.getType().createInputHandler( cnt );
    }

    protected java.io.InputStream input;
    protected Connection cnt;
    public InputHandler( Connection client ){
        //Save the connection to the client.
        cnt = client;
        try{
            //Make sure to get the input.
            input = cnt.getInputStream();
        }catch(java.io.IOException e){
            //The connection cannot be properly made?
            throw new UnknownRequestException( e );
        }
    }
    //Tells the server to stop processing or not.
    public abstract boolean keepAlive();
    //Aquires the next inputed request.
    public abstract String nextRequestString();

}
