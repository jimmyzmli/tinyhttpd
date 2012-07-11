package jserver;

/**
 * @author Jimmy
 */

import jserver.io.*;
import jserver.ui.*;
import jserver.ui.graphical.*;
import jserver.config.*;

import java.util.*;
import java.util.regex.*;

public class Main {

    private static String[] argNameList = {
        "GUI", "Help"
    };
    private static Map<String,Boolean> argFlags = new HashMap<String,Boolean>();
    private static String errMsg = "";

    static{
        //Add all of the possible arguments as NOT SET.
        for( String arg : argNameList ){
            argFlags.put( arg.toLowerCase(), false );
        }
    }

    private static void setArg( String argName ){
        argFlags.put( argName.toLowerCase() , true );
    }
    private static boolean hasArg( String argName ){
        if( argName == null ) return false;
        //Get whatever is stored in the flag
        Object flag = argFlags.get( argName.toLowerCase() );
        //Check to see if this argument even exsists.
        if( flag==null ) return false;
        //Check to see if this argument is used.
        return ( ((Boolean)flag)==true );
    }

    private static void processArgs( String[] args ){
        java.util.List<String> argList = new java.util.ArrayList<String>();
        //Process all the arguments.
        String argFormat = "[-/]\\w+";

        for( int i=0; i<args.length; i++ ){
            String arg = args[i];
            
            //Check to see if the argument is valid.
            if( ! arg.matches(argFormat) ){
                continue;
            }else
                arg = arg.substring(1);
            //Mark each valid argument as needed.
            if( argFlags.get( arg.toLowerCase() ) != null )
                setArg( arg );
            else
            //Warn the user about a unknown command.
                errMsg += "The command: "+arg+" is not valid.\n";
        }
    }

    /**
     * The server program entry point.
     * @param args Arguments for the program, which the user passes in at the commandline.
     */
    public static void main(String[] args) throws Exception{
        //Frist, process the arugments.
        //@TODO Finish argument processing.
        
        //Output the versioning & program info data.
        System.out.println( Const.GREET_MSG );

        //Process the arguments.
        processArgs( args );

        //Show all the errors.
        if( errMsg != null )
            System.out.println(errMsg);

        //Check to see if the user wanted help only.
        if( hasArg("help") ){
            System.out.println("Help:");
            //Show help.
            for( String command : argNameList )
                System.out.println( command );
            //Exit.
            return;
        }

        //Starts the servers
        //Read the default configeration file.
        Configeration defaultConfig = ConfigerationIO.readDefault();
        //Start the server process.
        JServer server = new JServer( defaultConfig );

        //Begin reading commands from input.
        //Start the command line interface. Using the standered input.
        if( ! hasArg("GUI") ){
            CommandLine cmd = new CommandLine( System.in, System.out );
        }else{
            //Or a GUI if the user requested one.
            new GUI();
        }

    }

}