package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AppServer {
    public static void main(String[] args) {

        // Declarations of time and color variables
        ServerColors color = new ServerColors();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();

        try {
            // Printing the server startup message
            System.out.println(color.PURPLE_BOLD_BRIGHT + "> " + color.RESET + color.YELLOW_BOLD_BRIGHT
                    + "Avvio del server... " + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now)
                    + color.RESET);
            
            // Declarations of ServerSocket and client array 
            ServerSocket connectSocket = new ServerSocket(4500);
            ArrayList<ClientConnected> clients = new ArrayList<ClientConnected>();
            
            // Infinite loop where the connection of a new client is accepted
            while (true) {
                Socket dataSocket = connectSocket.accept();
                
                // Declaration and start of the thread associated with the client socket
                ClientConnected clientAccept = new ClientConnected(dataSocket, clients, "");
                clientAccept.start();
            }
        } catch (Exception e) {
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE DURANTE L'ISTANZA DEL SERVER " + color.RESET
                    + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET);
        }
    }
}