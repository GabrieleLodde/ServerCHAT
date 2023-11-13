package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread{
    
    Socket clientSocket;
    ArrayList <ServerThread> partecipanti;

    public ServerThread(Socket s, ArrayList <ServerThread> array){
        this.clientSocket = s;
        this.partecipanti = array;
    }

    @Override
    public void run(){
        try {
            BufferedReader inDalClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outVersoIlClient = new DataOutputStream(clientSocket.getOutputStream());
            boolean esci = false;
            String stringaInviata = "";
            String stringaRicevuta = "";

            stringaRicevuta = inDalClient.readLine();
            System.out.println("Si e' connesso " + stringaRicevuta +  " !");


            while(!esci){
                stringaRicevuta = inDalClient.readLine();
                if(stringaRicevuta.equals("-1")){
                    esci = true;
                }
            }
            clientSocket.close();

        } catch (IOException e) {
            e.getMessage();
            System.out.println("Attenzione, errore nella creazione della comunicazione");
        }
    }
}