package jserver.header;

import jserver.config.Configeration;
import jserver.*;
import jserver.data.*;
import jserver.request.*;

import java.io.*; //For data transfer.

public class HttpHeader extends Header{
    /* The header of a HTTP response. Processes all the data given, and creates the string
     * apporpaitly */

    private StringBuilder header; //The final string.

    public HttpHeader( HttpRequest request, DataDescription data ){
        /* Processes the request and creates the header. */
        //Takes in the request information and the data to make this header for.
        /* General header format:
         * HTTP/1.1 200 OK
         * Content-Length: 100
         * Content-Type: text/html
         */

        //Storage.
        header = new StringBuilder("");

        //Makess sure that the data exist.
        if(!data.exists()){
            header.append("HTTP/1.1 404 Not Found\n");
            //Set the data to empty.
            data.clear();
        }else if( request.needDirMark ){
            header.append("HTTP/1.1 301 Moved Permanently\n");
        }else{
            header.append("HTTP/1.1 200 OK\n");
        }

        //Specify the content length so the data can be written properly.
        header.append("Content-Length: "+data.size()+"\n");

        //Set the data type.
        //Get the returned data file extension to determine the data type.
        String fName = request.webPath.toString();
        if( fName.lastIndexOf(".") == -1 ){
            //If no extension exsist, the type is default.
            header.append("Content-Type: "+Const.DEFAULT_DATA_TYPE+"\n");
        }else{
            String ext = fName.substring( fName.lastIndexOf(".")+1, fName.length() );
            header.append( "Content-Type: "+getDataType(ext)+"\n");
        }

        //Tell the client that the selected is now another directory.
        if( request.needDirMark ){
            //Tell the browser to update the URL base.
            //The requested dir should be interupretted as the requested base.
            header.append("Location: "+request.webPath.getBase()+"\n");
        }

        //Adds any extra header elements if needed.
        if( request.responseElements != null )
            header.append( request.responseElements );

        //Add the last line space to indicate data start.
        header.append("\n");
    }

    private String getDataType(String ext){
        /* Returns the HTTP format datatype of the extension given. For example, .HTML files
         * are the 'text/html' type. This data is looked up in a table. */
        String setType = Configeration.current().getDataType(ext); //The configed type.
        //Use the default type if the type is not found.
        if(setType == null)
            return Const.DEFAULT_DATA_TYPE;
        else
            return setType;
    }

    public @Override String toString(){
        /* Finializes the header string and gives it to the user */
        return header.toString();
    }
    public byte[] getBytes(){
        /* Gives the byte[] version of the header string. */
        return this.toString().getBytes();
    }

}
