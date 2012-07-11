package jserver.io;

import jserver.util.ServerUtil;
import jserver.*;

import java.io.*;

public class FileIO {
    /* Interfaces with the system, so that files can be accessed. SAFETY IS NOT A
     * PRIMARY CONCERN OF THIS CLASS! It should be handled by anyone whom uses this class.
     * This class ensures safef file acess but not security. */

    public static InputStream read( String fName ){
        //String adapter
        return read( new File(fName) );
    }
    public static InputStream read( File file ){
        /* Finds the file on disk and connect to it with a stream. This stream is then
         * given to the user for access. If the file doesn't exsist null is returned.*/
        InputStream fStream = null;
        try{
             fStream = new FileInputStream( file );
        }catch(FileNotFoundException e){
            //Indicates that file does not exsist.
            return null;
        }
        
        return fStream;
    }

    /**
     * Writes everything written in the input stream given to the file given. If the
     * file does not exsist, it will be created, then written to.
     * @param to The file to write to.
     * @param in The input to read from.
     * @return A boolean value to indicate weather the write was successful.
     */
    public static boolean write( File to, InputStream in ){
        //First, create a file stream for dumping.
        try{
            FileOutputStream out = new FileOutputStream( to );
            //Dump the data.
            ServerUtil.transferData( in , out );
            //Seal and flush the write.
            out.close();
        }catch(IOException e){
            //Indicate a write fail.
            return false;
        }
        //Indicate the write was sucessful, and all data was transfered.
        return true;
    }
}