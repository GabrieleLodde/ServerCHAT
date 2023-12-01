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
                ClientCollegato clientAccept = new ClientCollegato(dataSocket, partecipanti, "");
                partecipanti.add(clientAccept);
                clientAccept.start();
            }
        } catch (Exception e) {
            System.out.println("ERRORE DURANTE L'ISTANZA DEL SERVER");
        }
    }
}