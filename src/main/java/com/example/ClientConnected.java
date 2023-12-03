package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientConnected extends Thread {

    // Declaration of the variables used
    private Socket clientSocket;
    public ArrayList<ClientConnected> partecipanti;
    private String nickname;
    private BufferedReader inDalClient;
    private DataOutputStream outVersoIlClient;
    private boolean exit;
    private String messageClient;
    private DateTimeFormatter dtf;
    private LocalDateTime now;
    private ServerColors color;

    // Constructor
    public ClientConnected(Socket clientSocket, ArrayList<ClientConnected> partecipanti, String nickname) {
        this.clientSocket = clientSocket;
        this.partecipanti = partecipanti;
        this.nickname = nickname;
        this.exit = false;
        this.messageClient = "";
        this.dtf = DateTimeFormatter.ofPattern("HH:mm");
        this.now = LocalDateTime.now();
        this.color = new ServerColors();
    }

    @Override
    public void run() {

        // Calling the method to establish the two Input / Output channels
        instauraCanaliClient();
        // Infinite loop, until the client wants to exit
        do {
            try {
                // Reading the message coming from the client
                messageClient = inDalClient.readLine();
            } catch (IOException e) {
                System.out.println(color.PURPLE_BOLD_BRIGHT + "ERRORE NELLA RICEZIONE DEL MESSAGGIO " + color.RESET
                        + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            }
            // Series of checks relating to the syntactic nature of the message
            if (messageClient instanceof String && !messageClient.equals(null)) {

                // Check on the message containing a new nickname
                if (messageClient.split(":")[0].equals("@nick")) {
                    // Check the existence of the nickname
                    if (!checkNome(messageClient)) {
                        // Changing the client name and broadcasting the name itself
                        this.setNickname(messageClient.split(":")[1]);
                        inoltraMessaggioBroadcast(messageClient);
                    }
                }
                // Check whether the message should be broadcast
                else if (messageClient.split(":")[0].equals("@all")) {
                    inoltraMessaggioBroadcast(messageClient);
                }
                // Check if the message should be sent privately
                else if (messageClient.split(":")[0].equals("@only")) {
                    inoltraMessaggioPrivato(messageClient);
                }
                // Check whether to forward the list of connected customer names
                else if (messageClient.split(":")[0].equals("@lista")) {
                    inoltraLista(messageClient);
                }
                // Check if the client wants to leave the chat
                else if (messageClient.split(":")[0].equals("@exit")) {
                    inoltraMessaggioBroadcast(messageClient);
                    partecipanti.remove(getClientCollegato());
                    setExit(true);
                }
            }
        } while (!isExit());
        try {
            // Closing the socket corresponding to the client
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELLA CHIUSURA DEL SOCKET " + color.RESET
                    + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
        }
    }

    // Method for establishing channels to communicate
    public void instauraCanaliClient() {
        try {
            inDalClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INSTAURAZIONE DELLA COMUNICAZIONE DAL CLIENT "
                    + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
        }
        try {
            outVersoIlClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INSTAURAZIONE DELLA COMUNICAZIONE VERSO IL CLIENT "
                    + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
        }
    }

    // Method to set the client name
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Method to return the client name
    public String getNickname() {
        return this.nickname;
    }

    // Method to return the client itself
    public ClientConnected getClientCollegato() {
        return this;
    }

    // Method to return the outgoing channel to the client
    public DataOutputStream getOutVersoIlClient() {
        return this.outVersoIlClient;
    }

    // Method for setting the output variable
    public void setExit(boolean exit) {
        this.exit = exit;
    }

    // Method to return the output variable
    public boolean isExit() {
        return exit;
    }

    // Method to search the client index within the global array
    public int ricercaPartecipante(String privateNick) {
        for (int i = 0; i < this.partecipanti.size(); i++) {
            if (this.partecipanti.get(i).getNickname().equals(privateNick)) {
                return i;
            }
        }
        return -1;
    }

    // Method to check the existence of the nickname of the client who wants to join
    public boolean checkNome(String messageClient) {
        String nickClient = messageClient.split(":")[1];
        if (ricercaPartecipante(nickClient) == -1) {
            try {
                getOutVersoIlClient().writeBytes("@new:" + "\n");
            } catch (IOException e) {
                System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DELLA CONFERMA PER NICKNAME (@new) "
                        + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            }
            return false;
        } else {
            try {
                getOutVersoIlClient().writeBytes("@old:" + "\n");
            } catch (IOException e) {
                System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL NICKNAME RIDONDANTE (@old) "
                        + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            }
            return true;
        }
    }

    // Method to check if the client is alone, therefore the message will not be
    // sent in broadcast
    public boolean checkAlone(String textMessage) {
        if (this.partecipanti.size() == 1 && !textMessage.equals("*")) {
            try {
                getOutVersoIlClient().writeBytes("@alone1:" + "\n");
            } catch (IOException e) {
                System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO IN BROADCAST (@alone1) "
                        + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            }
            return true;
        }
        return false;
    }

    // Method to check if the client wants to send a broadcast message and has not
    // entered its nickname
    public void checkConfermaBroadcast(String textMessage) {
        if (!textMessage.equals("*")) {
            try {
                getOutVersoIlClient().writeBytes("@ok1:" + "\n");
            } catch (IOException e) {
                System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO DI CONFERMA (@ok1) "
                        + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            }
        }
    }

    // Method for forwarding the message in broadcast
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
                        System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO IN BROADCAST "
                                + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
                    }
                }
            }
        }
    }

    // Method to check if the client is alone, therefore the message will not be
    // sent privately
    public boolean checkAlonePrivate() {
        if (this.partecipanti.size() == 1) {
            try {
                outVersoIlClient.writeBytes("@alone2:" + "\n");
            } catch (IOException e) {
                System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@alone2) "
                        + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            }
            return true;
        }
        return false;
    }

    // Method for sending the client confirmation of forwarding the entered message
    // to the relevant searched client
    public void inoltraConfermaPrivata(String privateNick) {
        try {
            getOutVersoIlClient().writeBytes("@ok2:" + privateNick + "\n");
        } catch (IOException e) {
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO DI CONFERMA (@ok2) "
                    + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
        }
    }

    // Method for sending the client a warning that the nickname to contact is
    // missing in the chat
    public void inoltraErroreNickname(String privateNick) {
        try {
            getOutVersoIlClient().writeBytes("@wrong:" + privateNick + "\n");
        } catch (IOException e) {
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@wrong) " + color.RESET
                    + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
        }
    }

    // Method to forward the message privately
    public void inoltraMessaggioPrivato(String messageClient) {
        String privateNick = messageClient.split(":", 3)[2].split("#", 2)[0].toUpperCase();
        String textMessage = messageClient.split(":", 3)[2].split("#", 2)[1];
        int index = ricercaPartecipante(privateNick);
        if (index != -1) {
            if (!privateNick.equals(messageClient.split(":")[1])) {
                try {
                    this.partecipanti.get(index).getOutVersoIlClient()
                            .writeBytes("@only:" + messageClient.split(":")[1] + ":" + textMessage + "\n");
                } catch (IOException e) {
                    System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@only) "
                            + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
                }
                inoltraConfermaPrivata(privateNick);
            } else {
                try {
                    getOutVersoIlClient().writeBytes("@you:" + privateNick + "\n");
                } catch (IOException e) {
                    System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DEL MESSAGGIO PRIVATO (@you) " + "\n"
                            + color.RESET + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET);
                }
            }
        } else {
            if (!checkAlonePrivate()) {
                inoltraErroreNickname(privateNick);
            }
        }
    }

    // Method for forwarding the list of nicknames of the various clients present in
    // the array
    public void inoltraLista(String messageClient) {
        try {
            getOutVersoIlClient().writeBytes("@lista:" + getListaNicknames() + "\n");
        } catch (IOException e) {
            System.out.println(color.RED_BOLD_BRIGHT + "ERRORE NELL'INVIO DELLA LISTA AL CLIENT (@lista) " + color.RESET
                    + color.BLACK_BACKGROUND_BRIGHT + dtf.format(now) + color.RESET + "\n");
            ;
        }
    }

    // Method for creating a string with all the names of the various clients
    // present in the array
    public String getListaNicknames() {
        String lista = "";
        for (int i = 0; i < this.partecipanti.size(); i++) {
            lista += color.CYAN_BOLD_BRIGHT + this.partecipanti.get(i).getNickname() + ";";
        }
        return lista;
    }
}