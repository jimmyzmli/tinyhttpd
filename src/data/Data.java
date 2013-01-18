
package jserver.data;

import jserver.util.ServerUtil;
import jserver.*;   //Application-wide data.

import java.io.*; //Data transfer processing.

public class Data {
    /* The requested data by the user. This class supports stream to text translations and such. */

    ByteArrayOutputStream data = new ByteArrayOutputStream(); //The data storage.

    public Data( byte[] bytes ){
        //Store the byte into the storage.
        ServerUtil.transferData( new ByteArrayInputStream(bytes), data );
    }
    public Data( InputStream in ){
        //Transfer all data into the storage.
        ServerUtil.transferData( in, data );
    }
    public Data( String dataStr ){
        //Split up the string and send it into the storage.
        ServerUtil.transferData( new ByteArrayInputStream(dataStr.getBytes()), data );
    }

    public void reset(){
        //Recreate the storage.
        data = new ByteArrayOutputStream();
    }
    public void reset( InputStream in ){
        //Transfer ALL data from the IN stream to the storage.
        reset();
        ServerUtil.transferData( in , data );
    }
    public void reset( String dataStr ){
        reset();
        ServerUtil.transferData( new ByteArrayInputStream(dataStr.getBytes()), data );
    }


    public void append(InputStream in){
        ServerUtil.transferData( in , data );
    }


    //Data accessors.
    public byte[] getBytes(){
        //Returns the storage array.
        return data.toByteArray();
    }
    public String toString(){
        //Put the data into a string.
        return new String( getBytes() );
    }
    public ByteArrayOutputStream getRawStream(){
        //Returns the source.
        return data;
    }

}