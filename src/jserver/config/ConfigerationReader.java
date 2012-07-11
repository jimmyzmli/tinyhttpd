
package jserver.config;

import jserver.io.*;
import jserver.*;
import jserver.exception.*;

import java.io.*; //For file reading/processing (Buffering)
import java.util.*; //For input scanning.
import java.util.regex.*;

/**
* This class implemnts the configeration data processing part. It contains simple
* interfaces to read and write to configeration data sources. NO OTHER Classes should ever
* access the configeration settings directly.
s*/
public class ConfigerationReader {

    /**
     * Processes the configeration file format and alerts user of all errors. The parsed information
     * is stored in a "Configeration" object.
     */
    public static Configeration read( InputStream input ){

        //Create the inforamtion/setting storage.
        Configeration config = new Configeration();

        //Scan the data for settings.

        BufferedReader rdr = new BufferedReader( new InputStreamReader(input) );

        String line;
        try{
            while( (line=rdr.readLine()) != null ){
                //Process each line.

                //Determine the actual data.
                line = line.trim();
                //Determine if the line is actually useful.
                if( line.length() == 0 || line.charAt(0) == Const.CONFIG_COMMENT_CHAR ) continue;

                //If the data is a setting, then first rid of the comment, if any.
                int cmtAt = line.indexOf( Const.CONFIG_COMMENT_CHAR );
                if( cmtAt != -1 )
                    //If the comment exsists.
                    line = line.substring( 0, cmtAt );

                //Now only the setting exsists, determine the "directive" and the "value"
                int paramStart = line.indexOf(" ") + 1;
                //The setting is a UNARY SETTING if it has no value.
                if( paramStart == 0 )
                    continue;
                //If the value exsists, set it.
                String directive = line.substring( 0, paramStart - 1 );
                String value = line.substring( paramStart );

                //Handle the directive accordingly.
                for( DirectiveType type : DirectiveType.values() )
                    //If the directive is found.
                    if( type.toString().equalsIgnoreCase(directive) ){
                        //Let the handler handle it.
                        type.invoke( config, value );
                        //Then continue on.
                        continue;
                    }
                //If the directive is not found, then it might belong to a module.
                //So save it.
                config.addElement( directive, value );
            }
        }catch(IOException e){
            //Indicate that the config data couldn't be loaded.
            throw new SimpleMessage( "Cannot read the configeration properly from source "+input+" : "+e.getMessage() );
        }

        //Return the newly set settings.
        return config;
    }

    public static void write( Configeration config, OutputStream out ){
        /* Serializes the given configeration to a stream. If a mandatory setting is not filled then report
         * the error and stop and if needed undo write. */
        
    }
}