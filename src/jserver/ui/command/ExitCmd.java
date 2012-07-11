
package jserver.ui.command;

/**
 *
 * @author Jimmy
 */
public class ExitCmd {
    public static String invoke( String paramStr ){
        System.out.println("BYE");
        System.exit(0);
        return null;
    }
}
