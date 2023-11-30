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
        this.exit = false;
        this.messageClient = "";
    }

    @Override
    public void run() {
        try {
            instauraCanaliClient();
            do {
                messageClient = inDalClient.readLine();
                if (messageClient instanceof String && !messageClient.equals(null)) {
                    if (messageClient.split(":")[0].equals("@nick")) {
                        this.setNickname(messageClient.split(":")[1]);
                        inoltraMessaggioBroadcast(messageClient);
                    } else if (messageClient.split(":")[0].equals("@all")) {
                        inoltraMessaggioBroadcast(messageClient);
                    } else if (messageClient.split(":")[0].equals("@lista")) {
                        inoltraLista(messageClient);
                    } else if (messageClient.split(":")[0].equals("@only")) {
                        inoltraMessaggioPrivato(messageClient);
                    } else if (messageClient.split(":")[0].equals("@exit")) {
                        inoltraMessaggioBroadcast(messageClient);
                        partecipanti.remove(getClientCollegato());
                        setExit(true);
                    }
                }
            } while (!isExit());
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

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public ClientCollegato getClientCollegato() {
        return this;
    }

    public DataOutputStream getOutVersoIlClient() {
        return this.outVersoIlClient;
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public boolean checkAlone(String textMessage) {
        if (this.partecipanti.size() == 1 && !textMessage.equals("*")) {
            try {
                getOutVersoIlClient().writeBytes("@alone1:" + "\n");
            } catch (IOException e) {
                System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO IN BROADCAST (@alone1)");
            }
            return true;
        }
        return false;
    }

    public void checkConfermaBroadcast(String textMessage) {
        if (!textMessage.equals("*")) {
            try {
                getOutVersoIlClient().writeBytes("@ok1:" + "\n");
            } catch (IOException e) {
                System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO DI CONFERMA (@ok1)");
            }
        }
    }

    public void inoltraMessaggioBroadcast(String messageClient) {
        String nicknameClient = messageClient.split(":")[1];
        String textMessage = messageClient.split(":")[2];
        if (!checkAlone(textMessage)) {
            checkConfermaBroadcast(textMessage);
            for (int i = 0; i < this.partecipanti.size(); i++) {
                if (!this.partecipanti.get(i).getNickname().equals(nicknameClient)
                        && !this.partecipanti.get(i).getNickname().equals("")) {
                    try {
                        this.partecipanti.get(i).getOutVersoIlClient().writeBytes(messageClient + "\n");
                    } catch (IOException e) {
                        System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO IN BROADCAST (@all)");
                    }
                }
            }
        }
    }

    public boolean checkAlonePrivate() {
        if (this.partecipanti.size() == 1) {
            try {
                outVersoIlClient.writeBytes("@alone2:" + "\n");
            } catch (IOException e) {
                System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@alone2)");
            }
            return true;
        }
        return false;
    }

    public void inoltraErroreNickname(String privateNick) {
        try {
            getOutVersoIlClient().writeBytes("@wrong:" + privateNick + "\n");
        } catch (IOException e) {
            System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@wrong)");
        }
    }

    public void inoltraConfermaPrivata(String privateNick) {
        try {
            getOutVersoIlClient().writeBytes("@ok2:" + privateNick + "\n");
        } catch (IOException e) {
            System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO DI CONFERMA (@ok2)");
        }
    }

    public int ricercaPartecipante(String privateNick) {
        for (int i = 0; i < this.partecipanti.size(); i++) {
            if (this.partecipanti.get(i).getNickname().equals(privateNick)) {
                return i;
            }
        }
        return -1;
    }

    public void inoltraMessaggioPrivato(String messageClient) {
        String privateNick = messageClient.split(":")[2].split("#")[0];
        String textMessage = messageClient.split(":")[2].split("#")[1];
        int index = ricercaPartecipante(privateNick);
        if (index != -1) {
            try {
                this.partecipanti.get(index).getOutVersoIlClient()
                        .writeBytes("@only:" + messageClient.split(":")[1] + ":" + textMessage + "\n");
            } catch (IOException e) {
                System.out.println("ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@only)");
            }
            inoltraConfermaPrivata(privateNick);
        } else {
            if (!checkAlonePrivate()) {
                inoltraErroreNickname(privateNick);
            }
        }
    }

    public void inoltraLista(String messageClient) {
        try {
            getOutVersoIlClient().writeBytes("@lista:" + getListaNicknames() + "\n");
        } catch (IOException e) {
            System.out.println("ERRORE NELL'INVIO DELLA LISTA AL CLIENT");
            ;
        }
    }

    public String getListaNicknames() {
        String lista = "";
        for (int i = 0; i < this.partecipanti.size(); i++) {
            lista += this.partecipanti.get(i).getNickname().toUpperCase() + ";";
        }
        return lista;
    }
}