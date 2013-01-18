
package jserver.exception;

import jserver.*;

/**
 * This <code>Exception</code> indicates that a unreconizable or unprocessable connection
 * was made with the server.
 * @author Jimmy
 */
public class BadConnectionException extends SimpleMessage {
    
    public BadConnectionException( Connection errCnt ){
        setMsg( "Connection failed at port "+errCnt.getLink().getPort()+".");
    }
    public BadConnectionException( String msg ){ setMsg(msg); }

}
