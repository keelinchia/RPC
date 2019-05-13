#define _POSIX_SOURCE                                                
#include <sys/utsname.h>   
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <errno.h>
#include <netinet/in.h>
#include <netdb.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>
#include <pthread.h>
#include <time.h>
#include <sys/ioctl.h>
#include <net/if.h>

#define PORT_NUMBER 53953
#define PORT_NUMBER_2 35935

int read_one_byte(int client_socket, char *buffer);
int receiveFully(int client_socket, char *buffer, int length);
void printBinaryArray(char *buffer, int length);
int toInteger32(char *bytes);
void convertUpperCase(char *buffer, int length);

void err_sys(char *msg)
{
  perror(msg);
  exit(0);
}

/* Struct */
typedef struct
{
  int *time;
  char *valid;
} GET_LOCAL_TIME;

typedef struct
{
  char *OS;
  char *valid;
} GET_LOCAL_OS;

/* Functions */
void GetLocalTime(GET_LOCAL_TIME *ds);
void GetLocalOS(GET_LOCAL_OS *ds);
int uname(struct utsname *name);

/* Threads */
void* CmdProcessor(void* connfd);

/* Helper Functions */
void toBytes(int num, char* Bytes);
  
int main(int argc, char *argv[])
{
  int listenfd, connfd;
  socklen_t cli_length; 
  struct sockaddr_in serv_addr, cli_addr;
  struct hostent *client;
  
  /* Create a socket for communication. */
  if ((listenfd = socket(PF_INET, SOCK_STREAM, 0)) < 0) {
    err_sys("socket error");
  }

  /* Initialize server struct to zero. */
  memset((char *) &serv_addr, 0, sizeof(struct sockaddr_in));
  
  /* Specify the address family. */
  serv_addr.sin_family = AF_INET;

  /* Specify and convert the port number to network byte order. */
  serv_addr.sin_port = htons(PORT_NUMBER);

  /* Specify the IP address of the server. */
  serv_addr.sin_addr.s_addr = INADDR_ANY;

  /* Bind server and socket. */
  if (bind(listenfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
    err_sys("bind error");
  }
  
  /* Make socket listen with client queue of size 5. */
  if (listen(listenfd, 5) < 0) {
    err_sys("listen error");
  }

  printf("Listening...\n");

  int client_id = 0;
  pthread_t workers[5];
  pthread_attr_t attr;
  pthread_attr_init(&attr); /* Get the default attributes */

  for(;;) {
    cli_length = sizeof(cli_addr);

    /* Blocks until client connects. */
    connfd = accept(listenfd, (struct sockaddr *) &cli_addr, &cli_length);
    if (connfd < 0) {
      err_sys("accept error");
    }
 
    /* Get client. */
    client = gethostbyaddr((char *) &cli_addr.sin_addr, cli_length, AF_INET);

    printf("Connected client: %s\n", client->h_name);

    /* Launch a new thread to handle a client. */
    if (pthread_create(&workers[client_id], NULL, CmdProcessor, &connfd) != 0)
      printf("Failed to create thread\n");

    pthread_detach(workers[client_id]);
    
    client_id ++;  
  }
  
  return 0;
}

void* CmdProcessor(void* connfd)
{
  // Receive command header
  char header[104];
  char packet_length_bytes[4];
  char command[100];
  int cmd;
  
  receiveFully(*(int*) connfd, header, 104);
  printBinaryArray(header, 104);

  for (int i = 0; i < 100; i++) { 
    command[i] = header[i];
  }

  printf("command: ");
  for (int i = 0; i < 100; i++) {
    printf("%c", command[i]);
  }
  printf("\n\n");

  for (int i = 100; i < 104; i++) {
    packet_length_bytes[i - 100] = header[i];
  }

  // Get packet length
  int packet_length = toInteger32(packet_length_bytes);
  printf("packet_length_bytes = %d\n\n", packet_length);

  // Check validity of command
  if (strcmp(command, "GetLocalTime") == 0) {
    cmd = 0;
  }
  else if (strcmp(command, "GetLocalOS") == 0) {
    cmd = 1;
  }
  else {
    printf("Invalid command.\n\n");
    pthread_exit(0);
  }
  
  // allocate buffer to receive the data
  char *buffer = (char*)malloc(packet_length);
  receiveFully(*(int*) connfd, buffer, packet_length);
  printBinaryArray(buffer, sizeof(buffer));
  
  // Execute the command
  switch (cmd) {
  case 0: {
    GET_LOCAL_TIME *ds;
    ds = malloc(sizeof(GET_LOCAL_TIME));    
    ds->time = (int*)&buffer[0];
    ds->valid = &buffer[4];
    GetLocalTime(ds);
    free(ds);
    break;
  }
  case 1: {
    GET_LOCAL_OS *ds;
    ds = malloc(sizeof(GET_LOCAL_OS));
    ds->valid = &buffer[16];
    ds->OS = buffer;
    GetLocalOS(ds);
    free(ds);
    break;
  }
  }
  
  printf("Packet ready to send:\n");
  printBinaryArray(buffer, packet_length);
  
  /* send back */
  send(*(int*) connfd, header, sizeof(header), 0);
  send(*(int*) connfd, buffer, packet_length, 0);
  
  // release buffer
  free(buffer);
  
  pthread_exit(0);
}

void GetLocalOS(GET_LOCAL_OS *ds)
{
  struct utsname uts;
  
  if (uname(&uts) < 0) {
    perror("uname() error");
  }
  else {
    printf("Local OS: %s\n\n", uts.sysname);
  }
  
  char* sn = uts.sysname; 
  for (int i = 0; i < sizeof(sn); i++)
    {
      ds->OS[i] = sn[i];
    }
  
  printf("OS name array:\n");
  printBinaryArray((char*) ds->OS, sizeof(ds->OS));
  
  *(ds->valid) = 'T';
}

void GetLocalTime(GET_LOCAL_TIME *ds)
{
  time_t rawtime;
  struct tm * timeinfo;
  int t;
    
  time (&rawtime);
  timeinfo = localtime (&rawtime);
  //printf ("Current local time and date: %s", asctime(timeinfo));
  t = (timeinfo->tm_hour) * 100 + (timeinfo->tm_min);
  printf ("Current local time in hhmm: %d\n\n", t);
  
  uint32_t t_net = htonl(t);

  *(ds->time) = t_net;

  printf("Time array:\n");
  printBinaryArray((char*) ds->time, sizeof(ds->time));
  
  *(ds->valid) = 'T';
}
  
void convertUpperCase(char *buffer, int length)
{
  int i = 0;
  while (i < length)
    {
      if (buffer[i] >= 'a' && buffer[i] <= 'z')
	{
	  buffer[i] = buffer[i] - 'a' + 'A'; 
	}
      i++;
    }
}

int receive_one_byte(int client_socket, char *cur_char)
{
  ssize_t bytes_received = 0;
  while (bytes_received != 1)
    {
      bytes_received = recv(client_socket, cur_char, 1, 0);
    } 
	
  return 1;
}

int receiveFully(int client_socket, char *buffer, int length)
{
  char *cur_char = buffer;
  ssize_t bytes_received = 0;
  while (bytes_received != length)
    {
      receive_one_byte(client_socket, cur_char);
      cur_char++;
      bytes_received++;
    }
	
  return 1;
}

void printBinaryArray(char *buffer, int length)
{
  for (int i = 0; i < length; i++) { 
    printf("%d ", buffer[i]);
  }    
  
  printf("\n");
  printf("\n");
}

/* Bytes are read big endian */
int toInteger32(char *bytes)
{
  int tmp = (bytes[0] << 24) + 
    (bytes[1] << 16) + 
    (bytes[2] << 8) + 
    bytes[3];
	
  return tmp;
}

/* On an OS with a different endian from java, use this one 
int toInteger32(char *bytes)
{
  int tmp = bytes[0] + 
      (bytes[1] << 8) + 
      (bytes[2] << 16) + 
      (bytes[3] << 24);
	             
  return tmp;
}

*/
