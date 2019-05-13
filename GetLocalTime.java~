import java.io.*;
import java.net.*;

public class GetLocalTime {
    c_int time = new c_int();
    c_char valid = new c_char();
    
    public int execute(String IP, int port) throws IOException {
	
	/* Create binary buffer */
	int length = time.getSize() + valid.getSize();
	byte[] buf = new byte[104 + length];

	/* CmdID */
        String str = "GetLocalTime";
        byte[] CmdID = str.getBytes();

        for (int i = 0; i < CmdID.length; i++) {
            buf[i] = CmdID[i];
        }

        /* CmdLength */
        byte[] CmdLength = toBytes(length);

        for (int i = 100; i < 104; i++) {
            buf[i] = CmdLength[i - 100];
        }

	/* CmdBuf */
	byte[] CmdTime = time.toByte();
	for (int i = 104; i < time.getSize(); i++) {
            buf[i] = CmdTime[i - 104];
        }
	
	byte CmdValid = valid.toByte();
	buf[104 + length - 1] = CmdValid;
        
	printBinaryArray(buf, "Packet:");
	
	/* Create connection to RPC Server */
	
	//String serverIP = "10.27.18.245";
        
        // create a socket, which immediately connects to the server
        Socket clientSocket = new Socket(IP, port);
	
        // create text reader and writer
        DataInputStream inStream  = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());

        /* Send */
        outStream.write(buf, 0, buf.length);
        outStream.flush();
	
	/* Receive Header */
	byte[] CmdHeader = new byte[104];
	inStream.readFully(CmdHeader, 0, CmdHeader.length);
	printBinaryArray(CmdHeader, "Received Header:");
	
	/* Receive CmdBuffer */
	byte[] CmdBuffer = new byte[length];
       	inStream.readFully(CmdBuffer, 0, length);
	printBinaryArray(CmdBuffer, "Received CmdBuffer:");
	
	/* Set parameters according to the buffer */
	byte[] tm = new byte[time.getSize()];
	for (int i = 0; i < time.getSize(); i++) {
            tm[i] = CmdBuffer[i];
        }
	printBinaryArray(tm, "Time buffer:");
	time.setValue(toInteger(tm));
	
	System.out.println("Server's Local Time:" + " " + time.getValue());
	System.out.println("");
	
	char b = (char) CmdBuffer[CmdBuffer.length - 1];
      	valid.setValue(b);
	
	System.out.println("Valid:" + " " + valid.getValue());
	System.out.println("");
       
	return 0; 
    }

    public void setValid(char c) {
	this.valid.setValue(c);
    }    

    public c_int getTime() {
	return this.time;
    }

    public c_char getValid() {
	return this.valid;
    }

    static void printBinaryArray(byte[] b, String comment)
    {
        System.out.println(comment);
        for (int i=0; i<b.length; i++)
	    {
		System.out.print(b[i] + " ");
	    }
        System.out.println();
	System.out.println();
    }

    static private byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }

    static private int toInteger(byte[] b)
    {
	int result =    

       	    (b[0] << 24) & 0xff000000|
	    (b[1] << 16) & 0x00ff0000|
	    (b[2] << 8) & 0x0000ff00|
	    (b[3] << 0) & 0x000000ff;
	    
	return result; 
    }
}
