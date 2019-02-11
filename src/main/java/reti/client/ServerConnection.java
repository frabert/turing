package reti.client;

import java.io.IOException;
import reti.*;

/**
 * Connessione in background con il server
 */
public class ServerConnection implements Runnable {
  @Override
  public void run() {
    Connection conn = Client.connection;
    try {
      while(true) {
        conn.reader.read(conn.channel);
        Message msg;

        synchronized(conn.writer) {
          while((msg = conn.reader.getMessage()) != null) {
            conn.handleMessage(msg);
          }

          conn.writer.write(conn.channel);
        }
      }
    } catch(IOException ex) {
      DialogUtilities.showErrorDialogAndExit("Connessione con il server interrotta");
    }
  }

}