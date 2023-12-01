Chat Server-Client.

Descrizione del Programma:

Il programma è una semplice applicazione di chat basata su un'architettura server-client implementata in linguaggio Java. La comunicazione avviene tramite socket TCP, consentendo a più client di connettersi al server e partecipare a una chat in tempo reale.

Parte Server:
1. App.java
Il file App.java rappresenta il punto di ingresso del server. All'avvio, il server crea un socket in ascolto sulla porta 4500 e accetta connessioni dai client. Ogni client che si connette genera un nuovo thread (ClientCollegato) per gestire la comunicazione con quel particolare client.

2. ClientCollegato.java
La classe ClientCollegato estende la classe Thread e gestisce la comunicazione con un singolo client. Essa interpreta i comandi inviati dal client e gestisce varie operazioni, inclusi messaggi broadcast, messaggi privati, richieste di lista partecipanti e la gestione delle disconnessioni.

Parte Client:
1. App.java
Il file App.java è il punto di ingresso del client. Quando avviato, il client si connette al server sulla porta 4500 e inizializza un'istanza di ClientAzioni per gestire le interazioni con l'utente.

2. ClientAzioni.java
La classe ClientAzioni gestisce le azioni e le interazioni dell'utente. Consente all'utente di inserire un nickname, inviare messaggi broadcast, richiedere la lista dei partecipanti, inviare messaggi privati e uscire dalla chat. Un thread separato (ClientThread) gestisce la ricezione continua di messaggi dal server.

3. ClientThread.java
La classe ClientThread estende la classe Thread e gestisce la ricezione continua di messaggi dal server. I messaggi ricevuti vengono formattati e visualizzati in modo chiaro per l'utente.

Istruzioni per l'Esecuzione:

Server: 

- Eseguire il file App.java nella parte del server. Il server sarà in ascolto sulla porta 4500.

Client: 

- Eseguire il file App.java nella parte del client. Il client si connetterà al server sulla porta specificata.
- Inserire un nickname quando richiesto e utilizzare i comandi del menu per interagire con la chat.
- La chat permette di comunicare in tempo reale con altri partecipanti.

Comandi Disponibili:

/all: Invia un messaggio in broadcast a tutti i partecipanti.

/lista: Richiede e visualizza la lista dei partecipanti attuali.

/only: Invia un messaggio privato a un partecipante specifico.

/exit: Abbandona la chat.

Note:
- I comandi sono preceduti da un simbolo "@" per essere riconosciuti dal server.
- Il programma gestisce dinamicamente l'aggiunta e la rimozione di partecipanti alla chat.
- I messaggi sono formattati in modo chiaro per una migliore comprensione.
- I client non possono avere lo stesso nickname.


Autori.
Il progetto è stato sviluppato da:

Gabriele Lodde, Lorenzo Scarpulla, Tommaso Fallani.
