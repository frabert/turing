package reti.client;

import java.nio.channels.SocketChannel;

import reti.*;
import reti.client.states.*;

/**
 * Rappresenta la connessione al server
 */
public class Connection {
  /** Il canale di comunicazione col server */
  public SocketChannel channel;
  
  /** Il lettore con coda di messaggi */
  public MessageReader reader = new MessageReader();

  /** Lo scrittore con coda di messaggi */
  public MessageWriter writer = new MessageWriter();

  /** Il nome dell'utente connesso */
  public String username;

  ClientState state = new Started();

  public synchronized void handleMessage(Message msg) {
    state = state.handleMessage(msg);
  }

  public synchronized void handleAction(String action) {
    state = state.handleAction(action);
  }
}