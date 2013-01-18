
package jserver.config;

import jserver.*;
import jserver.exception.*;
import jserver.io.*;

import java.io.*;
import java.util.regex.*;

/**
 *
 * @author Jimmy
 */
public class SetMIMEDir {
    public static void invoke( Configeration config, String paramStr ){
        //Reads the given MIME file, and looks for the data.
        File file = new File( paramStr );

        //Check for the file's exsistense.
        if( !file.exists() || !file.isFile() )
            throw new InvalidSettingException("The MIME file given does not exsist!");
        
        BufferedReader rdr = new BufferedReader( new InputStreamReader( FileIO.read(file) ) );

        try{
            Matcher match = Pattern.compile("(\\S+)\\s+(.+)").matcher("");
            String line;
            while( (line=rdr.readLine()) != null ){
                if( line.length()==0 || line.startsWith("#") )
                    //Ignore comments and empty lines.
                    continue;
                //Process the line for an assignment
                match.reset(line);
                if( match.find() ){
                    //Assign if a assignment is found.
                    String type = match.group(1);
                    String[] exts = match.group(2).split(" ");

                    for( String ext : exts ){
                        config.addDataType( ext, type );
                    }
                }
                    
            }
        }catch(IOException e){
            throw new InvalidSettingException("The MIME file given is not in the right format. Please provide a valid MIME file.");
        }
    }
}
