
package jserver.request;

import jserver.*;

public class RequestParser {
    /* A class that controls all the inner implementation structure of the request processing
     * service. */
    
    public static Request parse( String requestStr, RequestType processor){
        /* If the type is already known, then directly process the request with that processor. */
        //Make sure that the type is REALLY known
        if( processor == RequestType.UNKNOWN )
            return parse( requestStr );
        else
            return processor.create( requestStr );
    }

    public static Request parse( String requestStr ){
        /* Determines the type of this request, and process it apporpiatly. */
        return HttpRequest.parse( requestStr );
    }
}
