
package jserver.ui.command;

import jserver.*;
import jserver.exception.*;

import java.lang.reflect.*;
/**
 *
 * @author Jimmy
 */
//@TODO Finish command line function access.
public enum Command {

    RECONFIG( ReconfigCmd.class ),
    EXIT( ExitCmd.class ),
    HELP( HelpCmd.class )
    ;

    private Method processor;
    Command( Class cls ){
        //Assignment
        try{
            processor = cls.getDeclaredMethod( "invoke", String.class );
        }catch( Exception e ){
            //Mark this command as unavalible.
            processor = null;
            throw new SimpleMessage("Failed to find the proper method in "+cls.getName()+". This command will be removed from the list.");
        }
    }

    public String invoke( String paramStr ){
        //Check to see if the command is avalible.
        if( processor == null ) return null;
        //Try to invoke the command.
        try{
            return (String)processor.invoke( null, paramStr );
        }catch( Exception e ){
            //Report the real error.
            throw new SimpleMessage("Failed to run command: "+toString()+". "+e.getCause().getMessage());
        }
    }

    //Manager methods, that manages the list of commands.

    public static Command forName( String cmdName ){
        //Finds the command that has the name given.s
        for( Command cmd : values() )
            if( cmd.toString().equalsIgnoreCase(cmdName) )
                return cmd;
        //Indicate if the command does not exist.
        return null;
    }
    public static String invokeFor( String cmdName, String paramStr ){
        //Get the right command.
        Command cmd = forName(cmdName);
        //Check to see if the command exsists.
        if( cmd != null )
            //Invoke it with the given parameters.
            return cmd.invoke( paramStr );
        else
            //If not return to indicate.
            return null;
    }
   
}
