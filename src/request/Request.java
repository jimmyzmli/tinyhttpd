
package jserver.request;

import jserver.data.*;

public abstract class Request {
    /* A request, being any typed (HTTP, FTP, etc.). This being the interface of a average Request. */

    public String responseElements;       //Any extra header elements needed to be sent to client.

    //Factory creation.
    public static Request parse( String requestString ){
        /* This is a placeholder method, and that the subclass
         * version should be called. This functionality being organizing the request data accoridingly. */
        //Use the parser to identify the request.
        return RequestParser.parse(requestString);
    }

    //This method gets/initializes/reads/access/creates (Any of these methods) the data specific to it's type
    //of request.
    public abstract Data getRequestedData();
    //Get a identifier of the request type.
    public abstract RequestType getType();
}
