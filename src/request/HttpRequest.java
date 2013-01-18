
package jserver.request;

import jserver.*;   //Configeration data support.
import jserver.io.*;    //For system access.
import jserver.data.*;  //Data access support.
import jserver.config.*;    //Configeration acess
import jserver.exception.*; //Specific error reporting

import java.io.*;   //For file processing.

public class HttpRequest extends Request{
	/* A data form that contains information on the HTTP header. With fields and no
	* accessors, for easy access. Since this is a one-time use class, it's okay. */

	/* GENERAL HTTP REQUEST FORMAT:
	GET /index.php HTTP/1.1
	Host: localhosts
	*/

	private static RequestType processorType = RequestType.HTTP;

        public enum HttpRequestType {
            //The types of avalible HTTP requests.
            POST, GET;
        }

	//Data form.
	public HttpRequestType type;//The request type - POST or GET.
	public boolean needDirMark = false;   //If the dir specification is ommited.
	public boolean usingDefault = false;  //If the default file is being used.
	public WebPath webPath = new WebPath(); //The requested virtual file path on the server.
	public float version;   //Beging HTTP 1.0 or 1.1
	public String host;     //The Host property (Name).
	public String[] postParams[];  //A list of the passed parameters. (GET or POST)
	public String[] getParams[];

	public class WebPath{
		//An abstraction of the typed URL in a browser (The requested File path).
		private String path;
		//The components of the path.
		private String file, base;

		WebPath(){}
		WebPath( String fullPath ){
			//Set the path.
			set(fullPath);
		}
		public void set( String fullPath ){
			//Leave everything as empty or unchanged if a path is not avalible.
			if( fullPath == null ) return;
			//Set the data.
			path = fullPath;
			splitPath( fullPath );
		}
		public void append( String append ){
			//Add and renews the path components.
			path += append;
			splitPath( path );
		}

		private void splitPath( String newPath ){
			//Split the web path into parts.
			int fileBegin = newPath.lastIndexOf("/") + 1;
			file = newPath.substring( fileBegin );
			base = newPath.substring( 0, fileBegin );
		}

		//Acessors
		public String getFile(){ return file; }
		public String getBase(){ return base; }
		public String getFull(){ return path; }
		@Override public String toString(){
			return getFull();
		}

	}

	/* A string processing class that contains only one interface, used to process the
	* HTTP request header data. This class is the interface to the outside requests.*/

	//Make sure no direct creation happenes.
	private HttpRequest(){}

	//Factory method.
	public static Request parse( String request ){
		/* Returns a data class with the given header request processed into catogories. */
		//The data to return.
		HttpRequest data = new HttpRequest();

		try{

			//Start to fill out the "form" :
			int pos = 0; //The current processing position in the request string.

			//First, get the request type. (GET, POST, HEAD, etc.)
			for( HttpRequestType type : HttpRequestType.values() )
			if( request.startsWith(type.toString()) ){ //All request types is captial... A enum propertie.
				data.type = type;
				//Update postion:
				pos += type.toString().length() + 1; //Skip the space... Exp: GET /index.php
			}

			//Get the req. file path.
			int pathEndPos = request.indexOf( " ", pos );
			data.webPath.set( request.substring( pos , pathEndPos ) );

			//Process the parameteres according to the types.
                        //Processing of GET parameters is needed for any type if it is a HTTP request.
			if( data.type != null ){
				//Get the parameter from the web path. (The ?param1=X&param2=Y format)
				int paramStart = data.webPath.toString().indexOf("?") + 1;
				if( paramStart != 0){
					//Get the parameter part.
					String paramStr = data.webPath.toString().substring(paramStart);
					//Update the web path from the parameters.
					data.webPath.set( data.webPath.toString().substring( 0, paramStart-1 ) );

                                        //Get the parameters from the text given.
                                        data.getParams = getParams( paramStr );
				}else{
					//No parameters...
				}
			}
                        if( data.type == HttpRequestType.POST ){
                            //Read the parameters at the end.
                            int paramStart = request.lastIndexOf("\n") + 1;
                            String paramStr = request.substring( paramStart );
                            //Set the parameter.
                            data.postParams = getParams( paramStr );
                        }

                        //Make sure that the path is decoded.
                        data.webPath.set( decodeURL( data.webPath.toString() ) );

			//@TODO Handle requested info's path.
			//Check to see if it is a protocal or a file path.
			if( data.webPath.toString().startsWith("/") ){
				//The given IS a path.

			}else{
				//Else the given is a protocal, which means the user wants to proxy.
                                
			}

			//Use the default file if the requested file is a directory.
			if( isDirectory(data.webPath.toString()) ){

				if(! data.webPath.toString().endsWith("/") ){
					data.webPath.set( data.webPath.toString()+"/" );
					//Need to tell the client that it's in a new dir.
					data.needDirMark = true;
				}
				data.webPath.append( Configeration.current().defaultPage );

				data.usingDefault = true;
			}
			pos = pathEndPos + 1;

		}catch(Exception e){
			throw new UnknownRequestException(e);
		}

		//Return final data.
		return data;

	}

	//Helper methods.
        /**
         * Decodes the browser encoded URL. Exp. %20 Translates to " " whitespace.
         * @param url
         * @return
         */
        private static String decodeURL( String url ){
            try{
                return java.net.URLDecoder.decode( url, "UTF-8" );
            }catch( UnsupportedEncodingException e ){
                throw new SimpleMessage( "Failed to decode URL ("+url+") : "+e.getMessage() );
            }
        }
	private static String[][] getParams( String paramStr ){
                //The final parameter list.
                String[] params[];

		//Split the param string into an associative array.
		String[] paramPairs = paramStr.split("&");
		//Create the actual storage.
		params = new String[paramPairs.length][];

		int i = 0;
		for( String paramPair : paramPairs ){
			//Split the pair into the KEY and VALUE.
			//Ignore if no equal sign exsists, because the value is not assigned.
			if( paramPair.length() == 0 )
			//If the the given have no KEY or VALUE, then ignore it.
			continue;
			if( ! paramPair.contains("=") ){
				//If the value does not have a SET operator. Default it's value to a EMPTY STRING
				params[i++] = new String[]{ paramPair, Const.EMPTY_STRING };
				continue;
			}

			params[i++] = paramPair.split("=", 2);
		}
		//Resize the array so that no skipped data exsists.
		params = java.util.Arrays.copyOf( params , i );

                return params;
	}

	private static boolean isDirectory( String fPath ){
		/* Finds out if the given after translation becomes a directory or not. */
		//First, find the real path.
		File real = new File( Configeration.current().rootDir + fPath );

		return (real.exists() && real.isDirectory());
	}

	//Create the data according to the request.
	public Data getRequestedData(){
		/* Gets the file data requested. */
		Configeration config = Configeration.current(); //The current config.

		ByteArrayOutputStream data =  new ByteArrayOutputStream();  //The storage.
		//First, determine the file.
		File requestedFile = new File(config.rootDir + webPath);
		InputStream fileStream = Cacher.readAsStream( requestedFile ); //The file stream.
		String errMsg = null; //The alternative output data, in the case of a error.

		//Check the propeties of the file. If the default file is not found, then list the dir instead.
		if( ! requestedFile.exists() && usingDefault )
		//A special requet to list the contents of the dir.
		return getDirData( new File(config.rootDir + webPath.getBase()) );

		//Take mesures if the data does not exsist.
		if( fileStream == null ){
			//The file is not found, so use the default NOT FOUND file.
			fileStream = Cacher.readAsStream(config.rootDir+config.errPage);
			if(fileStream == null){
				//If the NOT FOUND file is not found... Then use the error message.
				if(config.errMsg != null)
				errMsg = config.errMsg;
				else
				errMsg = Const.NOT_FOUND_MSG;
				//Since a error occured, return the message.
				return new Data(errMsg);
			}
		}else{
			//If the file is good, load it into memory.
			//Load the requested file data into memory.
			return new Data( fileStream );
		}

		//If the execution reaches this point, then the language is messed up.
		return null;
	}

	private Data getDirData( File dir ){
		/* Assumming that the path is valid and is a dir, and returns a HTML version of
		* the directory wrapped in a Data object. */
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try{
			//Write the header part of the template.
			data.write( Const.DIR_LIST_HEAD.getBytes() );
			for( String file : dir.list() ){
                            //Add the file into the finished list.
                            data.write( String.format( "<a href='%s' >%s</a><br />", webPath.getBase()+file, file ).getBytes() );
                        }
			//Write the ending.
			data.write( Const.DIR_LIST_TAIL.getBytes() );
			data.flush();
		}catch(IOException e){}
		//Return the finished HTML list of the dir.
		return new Data( data.toByteArray() );
	}

	public RequestType getType(){
		return processorType;
	}
}