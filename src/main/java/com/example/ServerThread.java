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
    String nicknameClient;

    public ServerThread(Socket s, ArrayList <ServerThread> array, String nome){
        this.clientSocket = s;
        this.partecipanti = array;
        this.nicknameClient = nome;
    }

    @Override
    public void run(){
        try {
            BufferedReader inDalClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outVersoIlClient = new DataOutputStream(clientSocket.getOutputStream());
            boolean esci = false;
            String stringaRicevuta = "";

            nicknameClient = inDalClient.readLine();
            System.out.println("> " + nicknameClient +  " si e' unito al gruppo!");

            while(!esci){
                stringaRicevuta = inDalClient.readLine(); //leggo cosa ha scelto il client
                
                if(stringaRicevuta.equals("1")){
                    stringaRicevuta = inDalClient.readLine(); //leggo il messaggio che un client ha intenzione di inviare a tutti 
                    
                    //System.out.println(stringaRicevuta);  //stampo il messaggio per verificare cosa ha inviato il client (non necessario) 
                    
                    outVersoIlClient.writeBytes(stringaRicevuta + "\n"); //invio il messaggio inviato in broadcast (in teoria)
                }
                if(stringaRicevuta.equals("-1")){
                    esci = true;
                    System.out.println("> " + nicknameClient + " ha abbandonato il gruppo");
                }
            }
            clientSocket.close();

        } catch (IOException e) {
            e.getMessage();
            System.out.println("> Attenzione, errore nella comunicazione");
        }
    }

    public String ricercaNome(ArrayList <ServerThread> array, String nickInserito){
        String nome = "";
        for(int i = 0; i < array.size(); i ++){
            if(nickInserito.equals(array.get(i).getNicknameClient())){
                nome = array.get(i).getNicknameClient(); 
            }
        }
        return nome;
    }

    public String getNicknameClient(){
        return this.nicknameClient;
    }
}