import java.io.*;
import java.net.*;

public class GetLocalOS {

    c_char[] OS = new c_char[16];
    c_char valid;

    public GetLocalOS() {
	
	for (int i = 0; i < 16; i++) {
	    OS[i] = new c_char();
	}
	
	valid = new c_char();
    }
    
    public int execute(String IP, int port) throws IOException {
	
        /* Create binary buffer */
        int length = OS.length + valid.getSize();
        byte[] buf = new byte[104 + length];
	
        /* CmdID */
        String str = "GetLocalOS";
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
        byte[] CmdOS = new byte[length - 1];
	for (int i = 0; i < 16; i++) {
	    CmdOS[i] = OS[i].toByte();
	}
	
        for (int i = 104; i < (length - 1); i++) {
            buf[i] = CmdOS[i - 104];
        }

        byte CmdValid = valid.toByte();
        buf[104 + length - 1] = CmdValid;

        printBinaryArray(buf, "Packet:");

        /* Create connection to RPC Server */ 

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
        byte[] os = new byte[length - 1];
        for (int i = 0; i < 16; i++) {
            os[i] = CmdBuffer[i];
        }
	
        printBinaryArray(os, "OS buffer:");

	for (int i = 0; i < (length -1); i++) {
	    OS[i].setValue(os[i]);
	}

	String s = "";
	
	for (int i = 0; i < (length - 1); i++) {
	    s += OS[i].getValue();
	}
	
        System.out.println("Server's Local OS: " + s);
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

    public c_char[] getOS() {
        return this.OS;
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
}
