
package jserver.handler;

import jserver.util.ServerUtil;
import jserver.*;
import jserver.exception.*;

import java.io.*;

/**
* Handles a HTTP input stream (At port 80), reading in the data accordingly.
* @author Jimmy
*/
public class HttpInputHandler extends InputHandler{

        boolean cont = true;

	public HttpInputHandler( Connection client ){
            super( client );
        }

	/**
	* Determines if the server should discard the connection that this handler is
	* handling.
	*/
	public boolean keepAlive(){ return cont; }
	/**
	* Returns the next request string sent from the connection.
	* @throws RuntimeException
	* @return String - The next part of the input given by the client.
	*/
	public String nextRequestString(){

            cont = false;
            try{

                String req = getRequestString( input );
                if( req.startsWith("POST") ){
                    //If the type is post, then extra data is needed.
                    //Find the content-length first.
                    String searchStr = "Content-Length: ";
                    int lenTxtPos = req.indexOf(searchStr);
                    if( lenTxtPos == -1 )
                        //If the extra argument text is not found, then just ignore the request.
                        return req;
                    else
                        lenTxtPos += searchStr.length();
                    int len = new Integer( req.substring( lenTxtPos, req.indexOf( "\n", lenTxtPos )-1 ) );

                    return req + ServerUtil.readData( input, len );
                }

                return req;
                    
            }catch( IOException e ){
                //Failed to read the input, the stream is not valid.
                throw new BadConnectionException(cnt);
            }

	}

	//Implementation/helper methods

	private String getRequestString( InputStream in ) throws IOException{
		/* Returns the request header that is sent over the input stream given. */
		//Since the request header ends with a new line, that's the end signal.
		//The storage
		String requestStr = new String("");
		//Test the string for the end.
		String line;
		//The end means the string is only a carraige return followed by a line space. ( "\r\n" )
		//Also except ("\n") as a EOS signal. Meaning the length is 0.
		//If the skipped line goes to 2 in a row, that means a submit is in place.
		int skippedLine = 0;
		while( (line = ServerUtil.getData( in, '\n' )) != null ){
			//Check for the end.
                    
			if( line.length() == 0 ){
				skippedLine++;
				if( skippedLine == 1 ) break;

			}else if( skippedLine > 0 ) skippedLine = 0;

			if( line.length() != 0 )
			if(line.charAt(0) == '\r') break;

			requestStr += line + "\n";
		}
                
		return requestStr;
	}
}
