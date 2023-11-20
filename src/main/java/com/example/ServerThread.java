package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread{
    
    Socket clientSocket;
    Socket sBroadcast;
    ArrayList <ServerThread> partecipanti;
    String nicknameClient;
    BufferedReader inDalClient;
    DataOutputStream outVersoIlClient;
    DataOutputStream outBroadcast;

    public ServerThread(Socket s, Socket sBroadcast, ArrayList <ServerThread> array, String nome){
        this.clientSocket = s;
        this.sBroadcast = sBroadcast;
        this.partecipanti = array;
        this.nicknameClient = nome;
    }

    @Override
    public void run(){
        try {
            inDalClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outVersoIlClient = new DataOutputStream(clientSocket.getOutputStream());

            outBroadcast = new DataOutputStream(sBroadcast.getOutputStream());

            boolean esci = false;
            String stringaRicevuta = "";

            nicknameClient = inDalClient.readLine();
            System.out.println("> " + nicknameClient +  " si e' unito al gruppo!");

            while(!esci){
                stringaRicevuta = inDalClient.readLine();
                
                if(stringaRicevuta.startsWith("@all:")){
                    stringaRicevuta = stringaRicevuta.substring(5);
                    stringaRicevuta = "> " + this.getNicknameClient() + " ha scritto " + stringaRicevuta; 
                    inviaATutti(stringaRicevuta);
                }
                else if(stringaRicevuta.equals("-1")){
                    esci = true;
                    System.out.println("> " + nicknameClient + " ha abbandonato il gruppo");
                }
            }
            clientSocket.close();
            sBroadcast.close();

        } catch (IOException e) {
            e.getMessage();
            System.out.println("> Attenzione, errore nella comunicazione");
        }
    }

    public String getNicknameClient(){
        return this.nicknameClient;
    }

    public DataOutputStream getOutVersoIlClient(){
        return this.outVersoIlClient;
    }

    public DataOutputStream getOutBroadcast() throws IOException{
        return this.outBroadcast;
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

    public void inviaATutti(String messaggioDaInviare){
        for(int i = 0; i < this.partecipanti.size(); i ++){
            try {
                this.partecipanti.get(i).getOutBroadcast().writeBytes(messaggioDaInviare + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("> Errore nell'invio del messaggio in broadcast");
            }
        }
    }
}