
package jserver.config;

/**
 *
 * @author Jimmy
 */
public class DefaultPageDir {
    public static void invoke( Configeration config, String paramStr ){
        config.defaultPage = paramStr;
    }
}
