
package jserver.header;

import jserver.data.*;  //The data source.

import java.io.*;

public class DataDescription {
    /* A piece of data. Describing it's properties such as size and etc. Used by
     * the header class to process general data. */
    
    //The storage device.
    private ByteArrayOutputStream data;

    public DataDescription(Data data){
        //Assingment constructor.
        this.data = data.getRawStream();
    }

    public void clear(){
        //Destorys the data DESCRIPTION. NOT THE ACTUAL DATA.
        //Create a empty dummy.
        data = new ByteArrayOutputStream();
    }
    //Property describers.
    public boolean exists(){
        //Checks if the data is present.
        return (data!=null);
    }
    public int size(){
        //The current data size.
        return data.size();
    }
    
}
