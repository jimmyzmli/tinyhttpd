
package jserver.exception;

import jserver.*;

/**
 * This class is meant to simplify error reporting. When this <code>Throwable</code> is thrown
 * or bubbles up, only the error message is printed and not the entire stack trace.
*/
public class SimpleMessage extends ServerException {

    protected String errMsg = "";

    public SimpleMessage(){}
    public SimpleMessage( String msg ){
        setMsg( msg );
    }

    public void setMsg( String msg ){
        if( msg != null )
            errMsg = msg;
    }

    //Overrides all the output methods so that no extream stack traces appear.

    @Override public void printStackTrace( java.io.PrintStream out ){
        //Outputs all to the error reporter.
        printStackTrace();
    }
    @Override public void printStackTrace( java.io.PrintWriter out ){
        //Outputs all to the error reporter.
        printStackTrace();
    }
    @Override public void printStackTrace(){
        /* It's best to not exagerate the error by printing a bunch of error traces. Thus, in the
         * case of an error "getting out", the user won't be petrified. */
        //Prints minimum error text to error report.
        Err.report( errMsg );
    }

}
