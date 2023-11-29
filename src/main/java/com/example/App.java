package com.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class App 
{
    public static void main(String[] args )
    {
        try {
            System.out.println("> Avvio del server....");
            ServerSocket connectSocket = new ServerSocket(4500);

            ArrayList <ClientCollegato> partecipanti = new ArrayList <ClientCollegato> ();
            
            while(true){
                Socket dataSocket = connectSocket.accept();

                ClientCollegato serverThread = new ClientCollegato(dataSocket, partecipanti, "");
                partecipanti.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) {
            e.getMessage();
            System.out.println("> Errore durante l'istanza del server!");
        }
    }
}