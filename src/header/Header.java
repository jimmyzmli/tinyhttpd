package jserver.header;

import jserver.*;
import jserver.request.*;

import java.lang.reflect.*; //Dynamic type creation.
import java.io.*; //For data transfer.

public abstract class Header{
    /* A header base, this class determines the type of header processor to use. According
     * to the given request. It also serves as a template to the other headers.*/

    private StringBuilder header; //The final string storage.

    //Must suppport the following final-data accessing functions.
    public abstract String toString();
    public abstract byte[] getBytes();

    //Dynamic factory.
    public static Header create( Request request, DataDescription data){
        /* This method from the properties of the request, determines the type of header to create
         * and return. */
        //Create the new header.
    
        //The type of header to create.
        Constructor factory = request.getType().responseCreator();
        try{
            
            return (Header)factory.newInstance( request, data );
            
        }catch(InstantiationException e){
            //The error is fatal if the correct class is not found.
            Err.fatal("The correct header file is not found, please reinstall the server.");
        }catch(Exception e){
            Err.fatal("Error creating the header template, please reinstall the server.");
        }
        //If the execution reaches here, then a seriouse error is in place...
        return null;
    }

}