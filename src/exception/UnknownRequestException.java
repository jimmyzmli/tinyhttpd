
package jserver.exception;

import jserver.*;
import jserver.request.*;

/**
 * This exception indicates that a exception is mismatched (As the name hints). That is,
 * the request is not correct for the port that it is sent to. Or that the port type is not
 * known at all.
 */
public class UnknownRequestException extends SimpleMessage {

    public UnknownRequestException( Exception e ){
         setMsg(e.getMessage());
    }
    public UnknownRequestException( Request errSrc ){
        //Store the given source formated.
        setMsg( getMessage()+" (Error source being a "+errSrc.getType()+")");
    }

}
