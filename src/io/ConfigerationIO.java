
package jserver.io;

import jserver.*;
import jserver.config.*;
import jserver.exception.*;

import java.io.*;

/**
 *
 * @author Jimmy
 */
public class ConfigerationIO extends ConfigerationReader {

    public static Configeration readDefault(){
        return read ( Const.DEFAULT_CONFIG_FILE );
    }

    public static Configeration read( String fileName ){
        return read( new File(fileName) );
    }

    public static Configeration read( File file ){
        //Make sure that the file exsist to avoid later error in File IO.
        if(! file.exists() ){
            Err.fatal("The configeration file provided "+file.getName()+" does not exsist. Try to use the backup default file.");
        }
        //No need to cache the configeration read. It's once only.
        InputStream fileSrc = FileIO.read( file );
        if( fileSrc==null ){
            throw new SimpleMessage("Bad configeration file, data cannot be read.");
        }

        return read( fileSrc );

    }
}
