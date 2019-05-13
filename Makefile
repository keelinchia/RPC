# the compiler: gcc for C program, define as g++ for C++
 CC = gcc
 JCC = javac
 # compiler flags:
 #  -g    adds debugging information to the executable file
 #  -Wall turns on most, but not all, compiler warnings
 CFLAGS  = -g -lpthread #-Wall
 JFLAGS = -g

 # the build target executable:
default: Server GetLocalTime.class  GetLocalOS.class c_int.class c_char.class Test.class

Server: Server.c
	 $(CC) $(CFLAGS) -o Server Server.c
		
GetLocalTime.class: GetLocalTime.java
	 $(JCC) $(JFLAGS) GetLocalTime.java

GetLocalOS.class: GetLocalOS.java
	 $(JCC) $(JFLAGS) GetLocalOS.java

c_int.class: c_int.java
	 $(JCC) $(JFLAGS) c_int.java

c_char.class: c_char.java
	$(JCC) $(JFLAGS) c_char.java

Test.class: Test.java
	$(JCC) $(JFLAGS) Test.java

clean:
	$(RM) Server
	$(RM) *.class
	$(RM) *~	

