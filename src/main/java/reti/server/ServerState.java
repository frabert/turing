package reti.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Lo stato del server
 */
public class ServerState {
  PersistentServerState persistent;
  EphemeralServerState ephemeral;

  /**
   * Ripristina uno stato precedente del server
   * @param stream Lo stream da cui ripristinare lo stato
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public ServerState(InputStream stream)
      throws IOException, ClassNotFoundException
  {
    ObjectInputStream oistream = new ObjectInputStream(stream);
    persistent = (PersistentServerState)oistream.readObject();
    oistream.close();

    ephemeral = new EphemeralServerState();
  }

  public ServerState() {
    persistent = new PersistentServerState();
    ephemeral = new EphemeralServerState();
  }

  /**
   * Salva lo stato attuale del server
   * @param stream Lo stream su cui salvare lo stato
   * @throws IOException
   */
  public void save(OutputStream stream)
      throws IOException
  {
    ObjectOutputStream oostream = new ObjectOutputStream(stream);
    oostream.writeObject(persistent);
    oostream.close();
  }

  public PersistentServerState getPersistent() { return persistent; }
  public EphemeralServerState getEphemeral() { return ephemeral; }
}