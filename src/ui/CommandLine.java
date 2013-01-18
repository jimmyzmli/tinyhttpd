
package jserver.ui;

import jserver.*;
import jserver.exception.*;
import jserver.ui.command.*;

import java.io.*;
/**
 *
 * @author Jimmy
 */
public class CommandLine implements Runnable {

    private static final String START_MARK = ">>";

    private BufferedReader input;
    private OutputStream output;
    private Thread runner;
    public CommandLine( InputStream in, OutputStream out ){
        //If the input is invalid, then do not do anything because there will be no input...
        if( in == null ) return;
        //Store the output stream for outputing... (Also buffer it)
        output = new BufferedOutputStream( out );
        //Create the input reader and start interfacing.
        input = new BufferedReader( new InputStreamReader(in) );
        //Start the interfacing thread, this thread must be persistent.
        runner = new Thread( this );
        //Begin:
        runner.start();
    }

    //Synchronize the input and output so that they don't interfere.
    public synchronized void write( String txt ) {
        try{
            output.write(txt.getBytes());
            //Make sure the text is written.
            output.flush();
        }catch( IOException e ){
            throw new SimpleMessage("The command line output failed to write. "+e.getMessage());
        }
    }
    public synchronized String read() {
        try{
            return input.readLine();          
        }catch( IOException e ){
            throw new SimpleMessage("The command line input failed to read. "+e.getMessage());
        }
    }

    public void run(){
        //This method contains the process that aquires and processes user given data.
        while( ! runner.interrupted() ){
            write(START_MARK);
            String cmdStr = read();
            //Skip this command if it cannot be read.
            if( cmdStr == null ) continue;
            cmdStr = cmdStr.trim();

            String cmd, paramStr;
            //Split the input into valid format.
            int cmdEnd = cmdStr.indexOf(" ");

            if( cmdEnd == -1 ){
                cmd = cmdStr;
                paramStr = Const.EMPTY_STRING;
            }else{
                cmd = cmdStr.substring( 0, cmdEnd );
                paramStr = cmdStr.substring( cmdEnd+1 );
            }
            
            String retVal = Command.invokeFor( cmd, paramStr );
            
            //Show the user the output.
            if( retVal == null )
                //But if the command is invalid, then notify the user.
                write("The command \""+cmd+"\" is invalid...");
            else
                write( retVal );
            //Write a new line to indicate the output end.
            write("\n");
        }
    }
    
}
