
package jserver.module;

import java.net.*;
import java.io.*;
/**
 *
 * @author Jimmy
 */
public class TestHandler{
        public static void handle( Socket cnt ) throws IOException {
            cnt.getOutputStream().write("HTTP/1.1 200 OK\nContent-Length:0\n\n".getBytes());
        }
}
