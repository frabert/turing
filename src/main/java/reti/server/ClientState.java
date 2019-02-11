package reti.server;

import reti.Message;
import reti.server.EphemeralServerState.ClientConnection;

/**
 * Rappresenta uno stato in cui si più trovare un utente collegato
 */
public abstract class ClientState {
  /**
   * Gestisce l'arrivo di un messaggio
   * @param msg Il messaggio arrivato da gestire
   * @param conn La connessione su cui il messaggio è arrivato
   * @return Il nuovo stato in cui si trova l'utente a seguito della gestione del messaggio
   */
  public abstract ClientState handleMessage(Message msg, ClientConnection conn);

  /**
   * Viene chiamato nel momento in cui un utente si disconnette mentre si trovava in questo stato
   * @param conn La connessione (non più valida) dell'utente che si è disconnesso
   */
  public void handleDisconnection(ClientConnection conn) {
    if(conn.account != null) conn.account.onDisconnect();
  }
}