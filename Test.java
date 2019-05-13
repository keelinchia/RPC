import java.io.*;

public class Test {
    
    public static void main(String args[]) throws IOException {

	String serverIP = "pyrite-n3";
	
	if (args.length != 0) {
	    serverIP = args[0]; // the ip from user input
	}
	
	/*
	c_int c = new c_int();
        c.setValue(14);
	System.out.println(c.getValue());
	System.out.println(c.getSize());
	*/

	/*
	c_char c = new c_char();
	c.setValue((byte)75);
	System.out.println(c.getValue());
	System.out.println(c.getSize());
	*/    

	/* Testing GetLocalTime */
	GetLocalTime obj = new GetLocalTime();
	obj.setValid('F');
	obj.execute(serverIP, 53953);

	/* Testing GetLocalOS */
	GetLocalOS obj2 = new GetLocalOS();
	obj2.setValid('F');
	obj2.execute(serverIP, 53953);
    }
}
