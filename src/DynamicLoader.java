
package jserver;

import jserver.util.ServerUtil;
import jserver.io.*;
import jserver.exception.*;

import java.io.*;	//For file processing. (Loading)
import java.util.*;
import java.util.regex.*;
import java.util.zip.*; //For loading JAR files.

public class DynamicLoader extends ClassLoader{
    /* Loads a module class from any path given. */
    
    public Class loadFrom( File path ){
        //Defaults to loading inner classes also
        return loadFrom( path, true );
    }
    public Class loadFrom( File path, boolean loadInner ){
        /* Gets the data of a class file and create the class of the file. If the file is
         * not found or not valid, then return null to indicate fail. This method specifies also
         * if the inner classes should be loaded also. */

        //The class does not exsist if the file doesn't exsist!
        if(! path.exists() || path.isDirectory() )
            return null;
        //Determine the file type.
        String fileExt = getFileType( path );
        
        if( fileExt.equalsIgnoreCase("jar") )
            //If the file is a JAR file, then everything of it must be loaded.
            return loadJar(path);

        ByteArrayOutputStream storage = new ByteArrayOutputStream(); //The class data storage.

        //Read the class file.
        ServerUtil.transferData( FileIO.read( path ), storage );

        //Load the class from the class data.
        byte[] data = storage.toByteArray();
        Class mod = null;

        try{
            mod = this.defineClass( data, 0 , data.length );
        }catch(ClassFormatError e){
            throw new ModuleFileException("Failed to load module ("+path.getAbsolutePath()+"): "+e.getMessage());
        }

        //Process the class if defined sucessfully.
        if( mod != null){
            //Loads all the inner classses also if nessary.
            if( loadInner )
                loadHelperMods( mod, path );
            //Make sure that all the links are loaded.
            resolveClass(mod);
        }

        return mod;

    }
    private void loadHelperMods ( Class mod, File path ){
        /* This method takes in the mod class and the path of the mod, so that it can
         * look for any inner classes of that mod class. Then load them. */
        //Loads all the "helper mods" as in inner classes and such.
        //The mod name.
        String modName = mod.getSimpleName();
        while( ! path.isDirectory() ){
            String pathStr = path.getAbsolutePath();
            //Takes off the file name so that it is a dir.
            path = new File( pathStr.substring( 0, pathStr.lastIndexOf(path.separator)+1 ) );
        }
        //The inner classes' names' pattern.
        Pattern pattern = Pattern.compile(modName+"$.+.class", Pattern.DOTALL );
        for( File file : path.listFiles() )
            if( file.getName().matches(modName+"\\$.+.class") )
                //Load the inner classes' files.
                loadFrom( file, false );

    }

    public Class loadJar( File path ){
        //Reads the classes in the jar files one by one
        List<Class> clsList = new ArrayList<Class>();   //The list of loaded classes.
        String mainClsName = null;
        Class mainCls = null;   //The main class of the JAR file.
        //Get the source of the JAR.
        ZipInputStream source = new ZipInputStream( FileIO.read(path) );
        BufferedInputStream reader = new BufferedInputStream( source );

        try{
            ZipEntry file;
            while( (file=source.getNextEntry()) != null ){
               //Create a class if the file is a class file.
               String fileExt = getFileType( file.getName() );
               if( fileExt.equalsIgnoreCase("class") ){
                    //Create a storage.
                    ByteArrayOutputStream storage = new ByteArrayOutputStream();
                    //Read the entry.
                    ServerUtil.transferData( reader, storage );
                    //Create a class from these data.
                    byte[] data = storage.toByteArray();
                    try{
                        clsList.add( defineClass( data , 0, data.length ) );
                    }catch(ClassFormatError e){
                        throw new ModuleFileException("Failed to load module ( in "+path.getAbsolutePath()+" ): "+e.getMessage());
                    }
               }
               //Or find the main class's name if the file is Manifest (.MF) file.
               else if( fileExt.equalsIgnoreCase("mf") ){
                    String fileText = "";
                    //Read the file (Which is ASCII text)
                    int datum;
                    while( (datum=reader.read()) != -1 )
                        fileText += (char)datum;
                    //Now find the Main-Class element in the text.
                    String searchLine = "Main-Class: ";
                    int infoPos = fileText.indexOf(searchLine) + searchLine.length();
                    mainClsName = fileText.substring( infoPos, fileText.indexOf('\n', infoPos)-1 );
               }
               //Otherwise just ignore.
            }
        }catch(IOException e){
            throw new ModuleFileException("ERROR! Jar file is not valid. The jar file "+path.getAbsolutePath()+" is not valid.");
        }

        //Now that all the information is aquired. Link all the classes and find the main class.
        if( mainClsName != null)
            for( Class cls : clsList ){
                //Link each class
                resolveClass( cls );
                //See if the class is the main.
                if( (cls.getSimpleName()+".class").equalsIgnoreCase(mainClsName) ){
                    mainCls = cls;
                    break;
                }
            }

        return mainCls;
    }

    public Class[] loadPackage( File dir ){
        //Create a storage for the classes.
        List<Class<?>> loadedPackage = new ArrayList<Class<?>>();
        //Load all of the CLASS files inside the given dir.
        for( File file : dir.listFiles() )
            if( getFileType(file.getAbsolutePath()).equalsIgnoreCase("class") )
                loadedPackage.add( loadFrom( file, false ) );
        //Return the package.
        return loadedPackage.toArray( new Class[0] );
    }

    //Helper methods.
    private String getFileType( File file ){ return getFileType( file.getName()); }
    private String getFileType( String fileName ){
        //Find the extension position.
        int extStart = fileName.lastIndexOf('.')+1;
        //If the extension doesn't exsist.
        if( extStart == 0 )
            return Const.EMPTY_STRING;
        //Return the extension.
        return fileName.substring( extStart );
        
    }
}
