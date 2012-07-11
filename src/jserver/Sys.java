
package jserver;

import java.io.*;   //For access of data.

public class Sys {
    /* A controller of the current operating system. This serves the access to basic functions and
     * information avaible accross all plateforms. */
    
    private Sys(){}; //The system is not a object, but a functionality.

    public static InputStream exec( String cmd ){
        //Default to does not wait for the exec.
        return exec( cmd, false );
    }
    public static InputStream exec( String cmd, boolean wait ){
        // Executes the given command on the system.
        //Run the command.
        Process process = null;
        try{
            process = new ProcessBuilder(cmd.split(" ")).start();
            if(wait)
                process.waitFor();
        }catch(IOException e){
            Err.report("Error executing command.");
        }catch(InterruptedException e){
            //Nothing bad should happen.
        }
        
        return process.getInputStream();
    }
    
}
