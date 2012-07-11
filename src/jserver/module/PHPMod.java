
package jserver.module;

import jserver.config.Configeration;
import jserver.*;
import jserver.request.*;
import jserver.data.*;

import java.io.*;

public class PHPMod {

	private static final String PARAM_SEP = "|P_SPLIT|";
	private static final String PHP_PREPEND = "<?php " +
	"$len = count($argv);" +
	"$i = 1;" +
	"for( ;$i<$len;$i+=2 ){" +
	"if($argv[$i] == \""+PARAM_SEP+"\" ){" +
	"$i++;break;"+
	"}"+
	"$_GET[$argv[$i]] = $argv[$i+1];" +
	"}" +
	"for( ;$i<$len;$i+=2 ){" +
	"$_POST[$argv[$i]] = $argv[$i+1];" +
	"}" +
	" ?>";

	private static String phpRoot = "C:/Program Files/PHP/";
	private static String phpDir = "C:/Program Files/PHP/php.exe";

	static{
		/* When this mod loads, change the PHP.ini file so that it loads an appended end signal file every time. */
		try{
			if(! new File(phpRoot+"PHPModPrepend.php").exists() ){
				//Create the append file.
				FileOutputStream out = new FileOutputStream(phpRoot+"PHPModPrepend.php");
				out.write(PHP_PREPEND.getBytes());
				out.flush();
				out.close();

				out = new FileOutputStream(phpRoot+"php.ini", true);
				out.write( ("\n[php]\nauto_prepend_file = \""+phpRoot+"PHPModPrepend.php\"").getBytes());
			}
		}catch(Exception e){ Err.fatal("PHP MOD INIT ERROR."); }
	}

	public static void process( final Request req, final Data data ){
		/* Processes the given data as required */
		// The given request should be a HTTP request, or else ignore the processing request
		if(! req.getClass().equals( HttpRequest.class ) ) return;
		//Use the request as a HTTP one.
		HttpRequest request = (HttpRequest) req;

		if( ! request.webPath.getFile().endsWith(".php") ) return;

		Configeration config = Configeration.current();

		if( ! new File(config.rootDir + request.webPath).exists() ) return;

		//Create the parameter string.
		String paramStr = "";
		paramStr += paramToString( request.getParams );
		paramStr += " \""+PARAM_SEP+"\" ";
		paramStr += paramToString( request.postParams );
                
		final InputStream result = Sys.exec("\""+phpDir+"\" -f \""+config.rootDir+request.webPath+"\" "+paramStr );
		try{
			Thread t = new Thread( new Runnable(){
				public void run(){
					data.reset( result );
				}
			} );
			t.start();
			java.util.concurrent.TimeUnit.MILLISECONDS.sleep(400);
			t.interrupt();
		}catch(Exception e){}
	}

	private static String paramToString(String[][] params){
                String paramStr = "";
		if( params != null )
                    for( String[] getParamPair : params )
                        for( String getParam : getParamPair )
                            paramStr += "\""+getParam+"\" ";
                else
                    paramStr = "";
                
                return paramStr;
	}

	private static String getData( InputStream in ) throws IOException {
		//An adapter using the default End Stream value, (-1).
		return getData( in, -1 );
	}
	private static String getData( InputStream in, int EOS ) throws IOException {
		//Reads all data from a stream, with the EOS (End Of Stream) char specified.
		//Make sure that the stream is not empty, or else indicate.
		if(in==null)
		return null;
		//The data storage.
		String data = new String("");

		//READ
		int datum;
		try{
			while( (datum=in.read()) != EOS )
			//Add to the data.
			data += (char)datum;
		}catch(IOException e){
			Err.report("Error in request data stream.");
			throw e;
		}

		//Gives the final data.
		return data.toString();
	}

}