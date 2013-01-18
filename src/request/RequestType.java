
package jserver.request;

import jserver.exception.UnknownRequestException;
import jserver.*;
import jserver.header.*;    //For some header properties.

import java.lang.reflect.*; //For dynamic processor creation.

public enum RequestType {
    /* A static list of all the avalible request handlers/processors. This static information
     * includes also the response type to a request. */

    UNKNOWN( Request.class, Header.class ),  //Use the basic types.
    HTTP( HttpRequest.class, HttpHeader.class )
    ;

    private Constructor responseFactory;
    private Class processorType;
    private Class responseType;
    private Method parser;     //The String processing method.
    RequestType( Class processorCls, Class headerCls ){
        //Saves the data given.
        processorType = processorCls;
        responseType = headerCls;
        try{
            //Locate the processor methods that this will proxy for.
            parser = processorCls.getDeclaredMethod( "parse", String.class );
            //Create the response type factory.
            responseFactory = responseType.getConstructors()[0];
        }catch(NoSuchMethodException e){
            //If the method is not found, that means one of the STATIC data is not correct, thus a fatal error.
            Err.fatal("Some of the execution files is not found... Please reinstall or reconfig the server. " +
                    "Also please check the version");
        }
    }
    public Request create( String requestStr ){
         /* Proxies the specific prossor type. */
        try{
            //Invoke the proxied method.
            return (Request)parser.invoke( null, requestStr );
        }catch(IllegalArgumentException e){
            //This is quite impossible, since the method is public and static.
            Err.fatal("A component is missing with the execution files... Please reinstall the server.");
        }catch(Exception e){
            //Peal off the invocation proxy error layer to find the real error.
            if( e.getCause() instanceof UnknownRequestException )
                 //Allow this reporting exception to reach the user.
                 throw (UnknownRequestException)e.getCause();
            Err.report("There are some problems executing a server plug, if this is happening multiple times, please reinstall the server.");
        }

        //Indicates error by null.
        return null;
    }

    //Accessors.
    public Class requestClass(){
        /* The class the processes the request */
        return processorType;
    }
    public Class responseType(){
        //Accesses the required resonse type to the type of request.
        return responseType;
    }
    public Constructor responseCreator(){
        //Gives the user the response type.
        return responseFactory;
    }
}
