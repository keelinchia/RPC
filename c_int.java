import java.lang.*; 
import java.io.*;

public class c_int{
    byte[] buf = new byte[4]; // little endian

    public int getSize() {    
	return this.buf.length;
    }

    public int getValue() {
	return toInteger(this.buf);
    }

    public void setValue(byte[] b) {
	for (int i = 0; i < 4; i++) {
	    this.buf[i] = b[i];
	}
    }

    public void setValue(int v) {
	byte[] result = toBytes(v);
	setValue(result);
    }

    public byte[] toByte() {
	return this.buf; 
    }

    /* Little Endian */
    static private byte[] toBytes(int i) {
	byte[] result = new byte[4];
	
	result[0] = (byte) (i /*>> 0*/);
	result[1] = (byte) (i >> 8);
	result[2] = (byte) (i >> 16);
	result[3] = (byte) (i >> 24);
	
	return result;
    }

    /* Little Endian */
    static private int toInteger(byte[] bytes) {
	return (bytes[0] & 0xFF)|
	    ((bytes[1] & 0xFF) << 8)|
	    ((bytes[2] & 0xFF) << 16)|
	    ((bytes[3] & 0xFF) << 24);
    }
    
    /*
    //Big Endian
    static private int toInteger(byte[] bytes) {
	return ((bytes[0] & 0xFF) << 24)|
	    ((bytes[1] & 0xFF) << 16)|
	    ((bytes[2] & 0xFF) << 8)|
	    (bytes[3] & 0xFF);
    } 
    */   
}

