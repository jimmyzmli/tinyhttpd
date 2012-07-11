
package jserver.io;

import jserver.*;
import jserver.data.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * Provides cached access to any types of data; so that there are no needs
 * to access the source multiple times. Measures will be taken to make sure no
 * heap space problem occurs, if in such a case - The source will be access directly
 * instead.
 * @author Jimmy
 */
public class Cacher {
    /* NOTE:There should only be one cacher in a run of the program so that no multi-cache
     * cases occur. (Thus, everything static) */

    private static Map<String,Data> dataList = new HashMap<String,Data>();
    /**
     * This method will add <b>explicitly</b> the data to the cache with the title given,
     * if the title already exsists, it will be replaced.
     * @param title The title (key) used to index the data.
     * @param data The data to cache.
     */
    public static void add( String title, InputStream data ){
         //Save the data in a wrapper.

         if( data == null || title == null )
             //No need to save the data if 's empty. (The map will automatically return so)
             return ;
         dataList.put( title, new Data(data) );
    }
    public static void add( String title, Data data ){
        //Save the already wrapped data.
        if( data != null )
            dataList.put(title, data);
    }

     /**
     * Reads a piece of cached data, if the data is not in memory, then the data will
     * be retrived as possible... In the case that the data cannot be retrived, the <code>null</code>
     * indicator will be returned.
     * @param title The title of the data to be read.
     */
    public static Data read( File file ){
        Data data = dataList.get( parseKey(file) );
        //Checks to see if the data exsists.
        if( data != null ){
            return data;
        }else{
            //Otherwise, try to read the file.
            if( ! file.exists() )
                //The file can't be read...
                return null;
            InputStream fileStream = FileIO.read(file);
            //Something's wrong with the file access.
            if( fileStream == null) return null;
            //Otherwise, save the file data.
            data = new Data( fileStream );
            //Save the data.
            add( parseKey(file), data );
            //Give the user the data.
            return data;
        }
        
    }
     /**
     * Reads a piece of cached data, if the data is not in memory, then NULL will be returned.
     * @param title The title of the data to be read.
     */
    public static Data read( String title ){
        //Check to see if the title can be a file
        File file = new File(title);
        if(  file.exists() )
            return read( file );
        //Or else return the stored data.
        return dataList.get( title );
    }
    //Access the data in different ways.
    public static String readAsString( String title ){
        Data data = read( title );
        if( data != null)
            return data.toString();
        else
            return null;
    }

    public static byte[] readAsByteArray(  File file ){
        Data data = read( file );
        if( data != null)
            return data.getBytes();
        else
            return null;
    }
    public static byte[] readAsByteArray( String title ){
        Data data = read( title );
        if( data != null)
            return data.getBytes();
        else
            return null;
    }

    public static InputStream readAsStream( File file ){
        Data data = read( file );
        if( data != null)
            return new ByteArrayInputStream( readAsByteArray(file) );
        else
            return null;        
    }
    public static InputStream readAsStream( String title ){
        Data data = read( title );
        if( data != null)
            return new ByteArrayInputStream( readAsByteArray(title) );
        else
            return null;
    }

    /**
     * This method parses different <code>Object</code>s into usable String titles.
     * @param file The Object to parse.
     * @return The Object as a String value key.
     */
    private static String parseKey( File file ){
        //Return a null key if it's null.
        if( file == null) return null;
        try{
            return file.getCanonicalPath();
        }catch(IOException e){
            //Since the chronical path cannot be created, encode it.
            return file.getName() + file.getUsableSpace() + file.getTotalSpace();
        }
    }
}
