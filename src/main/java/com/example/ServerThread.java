package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread{
    
    Socket clientSocket;
    Socket broadcastSocket;
    ArrayList <ServerThread> partecipanti;
    String nicknameClient;
    BufferedReader inDalClient;
    DataOutputStream outVersoIlClient;
    DataOutputStream outBroadcast;
    boolean exit;
    String exitString;
    String receivedString;
    String sendString;
    String nickString;
    String privateMessage;
    int indexClient;

    public ServerThread(Socket clientSocket, Socket broadcastSocket, ArrayList <ServerThread> partecipanti, String nicknameClient){
        this.clientSocket = clientSocket;
        this.broadcastSocket = broadcastSocket;
        this.partecipanti = partecipanti;
        this.nicknameClient = nicknameClient;
    }

    @Override
    public void run(){
        try {
            instauraCanaliClient();
            instauraCanaleBroadcast();
            inizializzaVariabili();
            inviaNicknameBroadcast();

            while(!exit){
                receivedString = inDalClient.readLine();
                
                if(receivedString.startsWith("/all:")){
                    inviaMessaggioInBroadcast();
                }
                else if(receivedString.startsWith("/lista:")){
                    receivedString = receivedString + ":" + getListaNicknames();
                    inviaAdUnClient();
                }
                else if(receivedString.startsWith("/verify:")){
                    verificaNome(receivedString.substring(8));
                }
                else if(receivedString.startsWith("/only:")){
                    inviaAdUnClient();
                }
                else if(receivedString.equals("/esci")){
                    uscitaDalGruppo();
                }
            }
            clientSocket.close();
            broadcastSocket.close();
        } catch (IOException e) {
            e.getMessage();
            System.out.println("> Attenzione, errore nella comunicazione");
        }
    }


    public void instauraCanaliClient(){
        try {
            inDalClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("> Errore nell'instaurazione della comunicazione dal client");
        }
        try {
            outVersoIlClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("> Errore nell'instaurazione della comunicazione verso il client");
        }
    }

    public void instauraCanaleBroadcast(){
         try {
            outBroadcast = new DataOutputStream(broadcastSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
             System.out.println("> Errore nell'instaurazione della comunicazione in broadcast");
        }
    }

    public void inizializzaVariabili(){
        exit = false;
        sendString = "";
        receivedString = "";
        exitString = "";
    }

    public void inviaNicknameBroadcast(){
        try {
            nicknameClient = inDalClient.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("> Errore durante la ricezione del nickname del client");
        }
        sendString = "> " + nicknameClient +  " si e' unito/a al gruppo!;";
        inviaATutti(sendString);
    }

    public String getNicknameClient(){
        return this.nicknameClient;
    }

    public void inviaMessaggioInBroadcast(){
        receivedString = receivedString.substring(5);
        sendString = "> " + this.getNicknameClient() + " ha scritto " + receivedString; 
        inviaATutti(sendString);
    }

    public void uscitaDalGruppo(){
        exitString = "> " + nicknameClient + " ha abbandonato il gruppo!;";
        inviaATutti(exitString);
        this.partecipanti.remove(getServerThreadCorrente());
        exit = true;
    }

    public String getListaNicknames(){
        String listaNicknames = "";
        for (int i = 0; i < this.partecipanti.size(); i++) {
            listaNicknames = listaNicknames + this.partecipanti.get(i).getNicknameClient() + ";";
        }
        return listaNicknames;
    }

    public DataOutputStream getOutVersoIlClient(){
        return this.outVersoIlClient;
    }

    public DataOutputStream getOutBroadcast() throws IOException{
        return this.outBroadcast;
    }

    public ServerThread getServerThreadCorrente(){
        return this;
    }

    public int ricercaIndiceNome(String nickInserito){
        int index = -1;
        for(int i = 0; i < this.partecipanti.size(); i ++){
            if(nickInserito.equals(this.partecipanti.get(i).getNicknameClient())){
                index = i; 
            }
        }
        return index;
    }

    public String ricercaNome(String nickInserito){
        String nome = "";
        for (int i = 0; i < this.partecipanti.size(); i++) {
            if(nickInserito.equals(this.partecipanti.get(i).getNicknameClient())){
                nome = nickInserito;
            }
        }
        return nome;
    }

    public void verificaNome(String nickInserito){
        String nome = ricercaNome(nickInserito);
        if(nome.equals("")){
            try {
                outVersoIlClient.writeBytes("/not" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("> Errore durante l'invio della risposta (not) relativa alla ricerca del nickname client");
            }
        }
        else if(nome.equals(this.nicknameClient)){
            try {
                outVersoIlClient.writeBytes("/you" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Errore durante l'invio della risposta (you) relativa alla ricerca del nickname client");
            }
        }
        else{
            try {
                outVersoIlClient.writeBytes("/ok" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Errore durante l'invio della risposta (ok) relativa alla ricerca del nickname client");
            }
        }
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

    public void inviaAdUnClient(){
        nickString = receivedString.split(":")[1];
        privateMessage = receivedString.split(":")[2];
        indexClient = ricercaIndiceNome(nickString);
        if(indexClient != -1){
            try {
                this.partecipanti.get(indexClient).getOutVersoIlClient().writeBytes(privateMessage + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("> Errore nella comunicazione privata (1)");
            }
        }
        else{
            System.out.println("> Errore nella comunicazione privata (2)");
        }
    }
}