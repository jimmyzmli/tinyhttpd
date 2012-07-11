
package jserver.module;

import jserver.*;
import jserver.request.*;
import jserver.data.*;

import java.lang.reflect.*; //Dynamic procedure call.

public enum ModType {
    // A list of all modules.
    PHP( PHPMod.class );

    private Method processor;
    ModType( Class processorCls ){
        /* Takes the processor class and use it to access the processing function. */
        try{
            processor = processorCls.getDeclaredMethod( "process", Request.class, Data.class );
        }catch(NoSuchMethodException e){
            Err.report("Error with module "+toString()+". The module is not valid, please try to reinstall it.");
            processor = null;   //Mark the processor as not avaible.
        }
    }

    public void process( Request req, Data data ){
        //A proxy for the processor function.
        //Make sure the processor is avalible.
        if(processor == null) return;
        try{
            processor.invoke( null, req, data );
        }catch(Exception e){
            Err.fatal("Something is wrong with module "+toString()+" execution.");
        }
    }

}
