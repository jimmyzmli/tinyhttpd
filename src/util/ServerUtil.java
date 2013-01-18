
package jserver.util;

import jserver.*;
import java.io.*; //Data transfer processing.
import java.nio.*;

public class ServerUtil {
	/* This is the chest of useful tools used by many parts of the server. It contains
	* methods on data processing/transfering that the server may call. */

        public static int transferData( InputStream in, OutputStream out){
            //The default EOS sign (Returned by IO systems).
            return transferData(  in, out, -1 );
        }
	public static int transferData( InputStream in, OutputStream out, int EOS ){
		/* Reads data from the input and write to the output. Then indicate how many bytes
		* was transferred. */
		int byteAmt = 0;

		try{
			//Transfer
			int datum;
			while( (datum=in.read()) != EOS ){
                                //Transfer one INT.
                                out.write(datum);
				//Keeps track of the data transfered.
				byteAmt++;
				//If the buffer is full, then force flush.
				if( byteAmt%Const.FLUSH_LIMIT == 0 )
				out.flush();
			}

		}catch(InterruptedIOException e){
                    //If the transfer is interuppted, then just return the current.
                    return byteAmt;
                }catch(IOException e){
			Err.report("Error in transfering data "+in.getClass()+" to "+out.getClass()+". "+e.getMessage());
			//Return a impossible value to indicate error. (Transfer failed)
			return -1;
		}
		return byteAmt;
	}

        public static int transferData( InputStream in, ByteBuffer out ){
            //The default EOS sign (Returned by IO systems).
            return transferData(  in, out, -1 );
        }
	public static int transferData( InputStream in, ByteBuffer out, int EOS ){
		/* Reads data from the input and write to the output. Then indicate how many bytes
		* was transferred. */
		int byteAmt = 0;

		try{
                        IntBuffer outTransfer = out.asIntBuffer();
			//Transfer
			int datum;
			while( (datum=in.read()) != EOS ){
                                //Transfer one INT.
                                outTransfer.put( datum );
				//Keeps track of the data transfered.
				byteAmt++;
                                //No need for flushing
			}

		}catch(InterruptedIOException e){
                    //If the transfer is interuppted, then just return the current.
                    return byteAmt;
                }catch(IOException e){
			Err.report("Error in transfering data "+in.getClass()+" to "+out.getClass()+". "+e.getMessage());
			//Return a impossible value to indicate error. (Transfer failed)
			return -1;
		}
		return byteAmt;
	}

        /**
         * Reads <i>len</i> amount of data from the input stream, then formats the data
         * as a <code>String</code>.
         * @param in The input stream.
         * @param len The amount of data to read.
         * @return The data read from the input stream as a String.
         */
        public static String readData( InputStream in, int len ){
            //Start reading 'len' times.
            String result = "";
            try{
                for( int i=0; i<len; i++ )
                    result += (char)in.read();
            }catch(IOException e){
                //Tell the user if a error occured.
                Err.report("Error occured while reading "+len+" bytes of data from a stream ("+in.getClass()+") : "+e.getMessage());
            }
            return result;
        }
        public static String getData( InputStream in ) throws IOException {
            //An adapter using the default End Stream value, (-1).
            return getData( in, -1 );
        }
        public static String getData( InputStream in, int EOS ) throws IOException {
            //Reads all data from a stream, with the EOS (End Of Stream) char specified.
            //Make sure that the stream is not empty, or else indicate.
            if(in==null)
                return null;
            //The data storage.
            String data = new String("");

            //READ
            int datum;
            try{
                while( (datum=in.read()) != EOS )
                    //Add to the data.
                    data += (char)datum;
            }catch(IOException e){
                Err.report("Error in request data stream.");
                throw e;
        }

            //Gives the final data.
            return data.toString();
        }

        public static InputStream substream( InputStream old, int EOS ){
            /* Returns the part of the stream until the EOS char is found. */
            //Transfer the stream.
            PipedInputStream subStream = new PipedInputStream();
            //Connect the output to the new storage.
            PipedOutputStream transfer = new PipedOutputStream();
            try{
                //Now all data will go to the new stream.
                transfer.connect( subStream );
                //Start the transfer.
                transferData( old, transfer, EOS );
                //The data is stored.
                transfer.close();
            }catch(IOException e){
                Err.report("The input stream is not valid, a substream was NOT created: "+e.getMessage());
                //Returns to indicate failiar.
                return null;
            }
            //Gives the new stream to the user.
            return subStream;
        }

}