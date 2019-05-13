import java.lang.*;
import java.io.*;

public class c_char{
    byte buf = 0; 

    public int getSize() {
        return (Byte.SIZE/Byte.SIZE);
    }

    public char getValue() {
        return (char)(this.buf);
    }

    public void setValue(byte b) {
	this.buf = b;
    }

    public void setValue(char c) {
        this.buf = (byte) c; 
    }

    public byte toByte() {
        return this.buf;
    }
}
