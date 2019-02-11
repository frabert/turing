package reti.client;

import reti.Message;

public abstract class ClientState {
  /**
   * Gestisce l'arrivo di un messaggi dal server
   * @param msg Il messaggio arrivato dal server
   * @return Il nuovo stato in cui il client si trova dopo aver gestito il messaggio
   */
  public abstract ClientState handleMessage(Message msg);

  /**
   * Gestisce una richiesta di azione da parte dell'utente, ad esempio
   * la pressione di un pulsante
   * @param action L'azione richiesta dall'utente
   * @return Il nuovo stato in cui il client si trova dopo aver gestito la richiesta
   */
  public ClientState handleAction(String action) {
    return this;
  }
}