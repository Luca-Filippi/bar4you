Applicazione android valida per l'esame di sistemi integrati e mobili, in particolare per il modulo del professor Livio Tenze.

Argomento a scelta non sviluppato a lezione: Firebase -> Real Time Database

L'applicazione dimostrativa consente la gestione delle ordinazioni di un ipotetico bar:

   -> MainActivity: è costituita da diversi frammenti che sono selezionabili tramite una barra di navigazione posizionata nella parte bassa dello schermo:
   
         -> HomeFragment consente l'inserimento del numero di tavolo, tale numero è selezionabile tramite uno spinner che mostra tutti i
         tavoli attualmente liberi
         
         -> PersonsFragment consente di insererire le persone che sono sedute al tavolo.
         
         -> OrderFragment visualizza il menù del bar diviso in 4 categorie birre, vini, cocktail e analcolici e consente all'utente di 
         selezionare gli artivoli che desidera ordinare.
         
         -> SendFragment consente di togliere elementi dall'ordine e di inviare l'ordine all'applicazione server.
         
         -> BillFragment consente di creare il conto o i conti, poi i clienti pagheranno alla cassa
         
  -> EndActicity visualizza il messaggio di andare a pagare la cassa e poi fa ripartire la MainActivity
  
  -> ErrorActivity visualizza messaggio di errore e poi fa ripartire la MainActivity
  
