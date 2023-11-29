package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientCollegato extends Thread {

    private Socket clientSocket;
    public ArrayList<ClientCollegato> partecipanti;
    private String nickname;
    private BufferedReader inDalClient;
    private DataOutputStream outVersoIlClient;
    private boolean exit;
    private String messageClient;

    public ClientCollegato(Socket clientSocket, ArrayList<ClientCollegato> partecipanti, String nickname) {
        this.clientSocket = clientSocket;
        this.partecipanti = partecipanti;
        this.nickname = nickname;
        this.messageClient = "";
    }

    @Override
    public void run() {
        try {
            instauraCanaliClient();
            inizializzaVariabili();

            do {
                messageClient = inDalClient.readLine();
                if (messageClient instanceof String && !messageClient.equals(null)) {
                    if (messageClient.split(":")[0].equals("/nick")) {
                        System.out.println(messageClient.split(":")[0]);
                        System.out.println(messageClient.split(":")[1]);
                        this.setNickname(messageClient.split(":")[1]);
                        inoltraMessaggio(messageClient);
                    } else if (messageClient.split(":")[0].equals("/all")) {
                        inoltraMessaggio(messageClient);
                    } else if (messageClient.split(":")[0].equals("/lista")) {
                        inoltraLista(messageClient);
                    } else if(messageClient.split(":")[0].equals("@only")){
                        inoltraMessaggioPrivato(messageClient);
                    }
                    else if (messageClient.split(":")[0].equals("/exit")) {
                        inoltraMessaggio(messageClient);
                        partecipanti.remove(this);
                        exit = true;
                    }
                }
            } while (!exit);
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
            System.out.println("> Attenzione, errore nella comunicazione");
        }
    }

    public void instauraCanaliClient() {
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

    public void inizializzaVariabili() {
        exit = false;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public ClientCollegato getServerThreadCorrente() {
        return this;
    }

    public DataOutputStream getOutVersoIlClient() {
        return this.outVersoIlClient;
    }

    public void inoltraMessaggio(String messageClient) {
        for (int i = 0; i < this.partecipanti.size(); i++) {
            if (!this.partecipanti.get(i).getNickname().equals(messageClient.split(":")[1]) && !this.partecipanti.get(i).getNickname().equals("")) {
                try {
                    this.partecipanti.get(i).getOutVersoIlClient().writeBytes(messageClient + "\n");
                } catch (IOException e) {
                    System.out.println("ERRORE NELL'INVIO DEL NICKNAME DEL NUOVO PARTECIPANTE");
                }
            }
        }
    }

    public void inoltraLista(String messageClient){
        try {
            getOutVersoIlClient().writeBytes("/lista:" + getListaNicknames() + "\n");
        } catch (IOException e) {
            System.out.println("ERRORE NELL'INVIO DELLA LISTA AL CLIENT");;
        }
        
    }

    public String getListaNicknames(){
        String lista = "";
        for (int i = 0; i < this.partecipanti.size(); i++) {
            lista += this.partecipanti.get(i).getNickname() + ";";
        }
        return lista;
    }

    public void inoltraMessaggioPrivato(String messageClient){
        if(this.partecipanti.size() <= 1){
            try {
                outVersoIlClient.writeBytes("@alone:" + "\n");
            } catch (IOException e) {
                System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO @ALONE");
            }
        }
        else{
            boolean trovato = false;
            for (int i = 0; i < this.partecipanti.size(); i++) {
                if(this.partecipanti.get(i).getNickname().equals(messageClient.split(":")[2])){
                    try {
                        trovato = true;
                        this.partecipanti.get(i).getOutVersoIlClient().writeBytes("@only:" + messageClient.split(":")[1] + ":" + messageClient.split(":")[2] + "\n");
                    } catch (IOException e) {
                        System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO");
                    }
                }
            }
            if(!trovato){
                try {
                    outVersoIlClient.writeBytes("@wrong:" + "\n");
                } catch (IOException e) {
                    System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO @WRONG");
                }
            }
        }
    }
}