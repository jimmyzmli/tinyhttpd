/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jserver;

/**
 *
 * @author Jimmy
 */
public class Err {
    /* For central error processing, all errors are reported through this interface */
    
    public static void report(String msg){
        System.out.println(msg);
    }
    public static void fatal( String msg ){
        //Alert user of situation
        System.out.println(msg);
        //Exit...
        System.out.println("Server will exit...");
        System.exit(0);     //Normal exit.
    }
}
