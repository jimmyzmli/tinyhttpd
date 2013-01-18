
package jserver.exception;

/**
 *
 * @author Jimmy
 */
public class InvalidSettingException extends SimpleMessage {

    public InvalidSettingException( String errMsg ){ super("Error in setting: "+errMsg); }
    
    @Override public void printStackTrace(){
        super.printStackTrace();
        //Exit the program, because without information, the server cannot start.
        System.exit(0);
    }
    
}
