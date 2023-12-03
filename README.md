# Colored Chat Client-Server (Notes Server Side)
---
## Technologies:

The program was developed with Visual Studio Code software and was written exploiting the potential of the Java language.

---
## Basic operation of the program:

The program represents a client-server application that simulates a chat, by sending commands from client to server and vice versa, with the related printouts based on the type of command received/sent.

## Server Side (classes): 

### 1. AppServer.java:
   
The AppServer.java class represents the entry point of the server. Upon startup, the server creates a socket listening on port 4500 and accepts connections from clients. Each client that connects generates a new thread (ClientConnected) to manage communication with that particular client, in order to manage multi-threads operation.

### 2. ClientConnected.java:
   
The ClientConnected.java class extends the Thread class and handles communication with a single client. It interprets commands sent by the client and handles various operations, including broadcast messages, private messages, participant list requests and disconnect management. For each functionality there are checks relating to the feasibility of these operations, since the client could have sent any type of message and the server must be able to handle any situation.

### 3. ServerColors.java:

The ServerColors.java class is used to display each print on the terminal with criteria relating to the type of print.

---
## Instructions for Execution:

### Server side: 

- Run the AppServer.java file on the server side. The server will be listening on port 4500.
- For each command received, the server has specific methods that allow linear communication with the client.

### Commands sent Server Side:

@new: This command identifies a confirmation message relating to the nickname entered by the client

@old: This command identifies an error message related to entering a nickname that already exists

@nick: This command identifies a message joining a new client to the group, which is forwarded by the server to all connected clients

@alone1: This command identifies an error message related to sending a broadcast message when the client is alone in the chat

@ok1: This command identifies a confirmation message relating to the sending of a broadcast message

@all: This command identifies a broadcast message with text

@alone2: This command identifies an error message related to sending a private message when the client is alone in the chat

@you: This command identifies an error message related to sending a private message when the client has entered its own nickname

@wrong: This command identifies an error message related to sending a private message when the client has entered a nickname of a client that is not present

@ok2: This command identifies a confirmation message relating to the sending of a private message

@only: This command identifies a private message with text

@lista: This command identifies a message containing the list of nicknames of the various connected clients

@exit: This command identifies a client leaving the group message, which is forwarded by the server to all connected clients

## Programming notes:

- The commands are preceded by an "@" symbol to be recognized by both the server and the client for printing purposes.
- The program dynamically manages the addition and removal of clients.
- Messages are clearly formatted in the ClientThread class for better understanding.
- Clients cannot have the same nickname, otherwise the server sends the client a further request to change it.
- Every single print on the terminal is characterized by precise colors, which have been pre-selected based on specific criteria.

## Color criteria
- Thrown exceptions or general errors are colored "red".
- Each current hour of the various messages is "white" on a "black background".

## Programmers who created the "Colored Chat"

The program was developed by:

Gabriele Lodde, 
Lorenzo Scarpulla, 
Tommaso Fallani
