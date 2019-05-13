import java.net.*;
import java.io.*;  

public class client_test
{
    public static void main(String args[]) throws IOException
    {
	String serverIP = "10.27.18.245";

	/*
        if (args.length != 0)
        {
            serverIP = args[0]; // the ip from user input
        }
	*/
        
        // create a socket, which immediately connects to the server
        Socket clientSocket = new Socket(serverIP, 53953); 

        // create text reader and writer
        DataInputStream inStream  = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());

        // prepare a binary buffer
	byte[] buf = new byte[104];

	byte[] cmd = args[0].getBytes();

	String str = "THE REAL DATA.";
	byte[] buffer = str.getBytes();

	for (int i = 0; i < cmd.length; i++) {
	    buf[i] = cmd[i];
	}
	
	// Get buffer length in bytes
        byte[] bufLengthInBinary = toBytes(buffer.length);

	for (int i = 100; i < buf.length; i++) {
	    buf[i] = bufLengthInBinary[i - 100];
	}   
	    
	printBinaryArray(buf, "Packet length:");
	
	// send command header
        outStream.write(buf, 0, buf.length);
        outStream.flush();
	
        // send the command data
        outStream.write(buffer, 0, buffer.length);
        	
        //read the data back
	//inStream.readFully(bufLengthInBinary); // ignore the first 4 bytes
        //inStream.readFully(buf); // 
     
        // convert the binary bytes to string
	//  String ret = new String(buf);
        
        // should be all upcases now
	// System.out.println(ret);
    }
	
    static void printBinaryArray(byte[] b, String comment)
    {
        System.out.println(comment);
        for (int i=0; i<b.length; i++)
        {
            System.out.print(b[i] + " ");
        }
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
}
