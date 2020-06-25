# SitoRaccomandazione
Progetto tesi di laurea: 

Sistema per la raccomandazione di musica basato su logiche descrittive, probabilità e combinazione di prototipi

## Descrizione del contenuto della repository

In questo repository troviamo il progetto che contiene il sito di raccomandazione.

Nella cartella “𝑙𝑖𝑏” troviamo:
* il file “𝑗𝑠𝑜𝑛 − 20200518.𝑗𝑎𝑟” che è la libreria “𝑜𝑟𝑔.𝑗𝑠𝑜𝑛” utilizzata per la lettura dei JSON.
Nella cartella “𝑠𝑟𝑐” troviamo:
* la cartella “𝑐𝑙𝑎𝑠𝑠𝑒𝑠” che contiene: la classe “𝑅𝑒𝑐𝑜𝑚𝑚𝑒𝑛𝑑𝑒𝑟” che ha il ruolo di raccomandare le canzoni per un genere combinato e la classe “𝑃𝑎𝑖𝑟” in supporto alla classe “𝑅𝑒𝑐𝑜𝑚𝑚𝑒𝑛𝑑𝑒𝑟”;
* la cartella “𝑠𝑜𝑛𝑔𝑠” contiene le canzoni con le loro proprietà;
* il file “𝑑𝑎𝑡𝑎” è un file di tipo JSON contenente tutti i dati reperiti da AllMusic;
* classe “𝑇𝑒𝑠𝑡” utilizzata per provare la classe “𝑅𝑒𝑐𝑜𝑚𝑚𝑒𝑛𝑑𝑒𝑟”.

Nella cartella “𝑤𝑒𝑏” troviamo:
* la cartella “𝑐𝑜𝑐𝑜𝑠_𝑔𝑒𝑛𝑟𝑒𝑠” nella quale troviamo la lista dei nostri generi di partenza;
* la cartella “𝑝𝑟𝑜𝑡𝑜𝑡𝑖𝑝𝑖” contiene tutti i file di input per CoCoS, e quindi anche il risultato fornito in seguito alla combinazione dei generi;
* il file “𝑖𝑛𝑑𝑒𝑥.𝑗𝑠𝑝” è la home page del nostro sito di raccomandazione, per la sua realizzazione abbiamo utilizzato le JSP e Bootstrap.

## Esecuzione

Prima di eseguire il sito web si può copiare i nuovi output nelle cartelle corrette del progetto web, che sono:
* il file “𝑑𝑎𝑡𝑎” e la cartella “𝑠𝑜𝑛𝑔𝑠” sono da copiare nella cartella “𝑠𝑟𝑐”;
* le cartelle “𝑝𝑟𝑜𝑡𝑜𝑡𝑖𝑝𝑖” e “𝑐𝑜𝑐𝑜𝑠_𝑔𝑒𝑛𝑟𝑒𝑠” nella cartella “𝑤𝑒𝑏”.