
package jserver;

/**
 * This class provides global application wide constant data.
 * @author Jimmy
 */
public interface Const {

    String APP_NAME = "JServer 1.0";
    String GREET_MSG = String.format( "Starting %s... Created by Jimmy.", APP_NAME );

    String EMPTY_STRING = "";

    String DEFAULT_CONFIG_FILE = "config.ini";
    String DEFAULT_DATA_TYPE = "text/plain";
    String NOT_FOUND_MSG = "<i><h3>File not found.</h3></i>";
    String UNKNOWN_REQUEST_MSG = "A request is not identified by the server, request ignored.";

    String DIR_LIST_HEAD = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\"><html><head></head><body>"+
                           "<pre style='padding:5;' ><hr>";
    String DIR_LIST_TAIL = "<hr></pre><address>Directory listing service at port 80</address></body></html>";      //The HTML head and tail of the dir listing template.

    char CONFIG_COMMENT_CHAR = '#';
    char CONFIG_ELE_SEPERATOR = 2;  //The charater used to sperate config elements combined into a string.

    int HANDLER_AMT = 30;
    int FLUSH_LIMIT = 20;           //The average flush limit when writing to a stream.
}
