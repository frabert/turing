package reti.server;

import java.net.InetAddress;
import java.util.*;

import reti.MessageReader;
import reti.MessageWriter;
import reti.server.states.Started;

/**
 * Stato del server che viene creato ogni sessione e distrutto al termine
 */
public class EphemeralServerState {

  /**
   * Informazioni mantenute per ogni client connesso
   */
  public class ClientConnection {
    public MessageReader reader = new MessageReader();
    public MessageWriter writer = new MessageWriter();
    public ClientState state = new Started();
    public Account account;
  }

  public Hashtable<Account, ClientConnection> connections = new Hashtable<>();

  /** Consente di sapere se un indirizzo multicast è già stato utilizzato o meno */
  public HashSet<InetAddress> generatedAddresses = new HashSet<>();

  /** Mantiene gli indirizzi multicast generati e non più utilizzati */
  public Stack<InetAddress> unusedAddresses = new Stack<>();

  /**
   * Restituisce un indirizzo multicast per chat di gruppo
   */
  public InetAddress getFreeChatAddress() {
    if(unusedAddresses.isEmpty()) {
      return RandomUtils.genRndMulticastAddress();
    } else {
      return unusedAddresses.pop();
    }
  }
}