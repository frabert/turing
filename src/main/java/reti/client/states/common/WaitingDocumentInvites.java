package reti.client.states.common;

import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;

/**
 * Stato del client che attende la lista degli inviti ricevuti
 * dal server
 */
public class WaitingDocumentInvites extends ClientState {
  String username, document;
  int count, sections;
  int received = 0;
  ClientState previous;

  public WaitingDocumentInvites(int count, ClientState previous) {
    this.count = count;
    this.previous = previous;
  }
  
  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_INVITE_USER_NAME: {
        username = msg.getString();
        return this;
      }
      case Message.TYPE_INVITE_USER_DOCUMENT: {
        document = msg.getString();
        return this;
      }
      case Message.TYPE_INVITE_USER_SECTIONS: {
        int sectionCount = msg.getInt();
        
        final Document doc = new Document(document, username, sectionCount);

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            Client.frame.addAvailableDocument(doc);
          }
        });
        received++;
        if(received == count) {
          DialogUtilities.showInfoDialog("Hai ricevuto nuovi inviti");
          return previous;
        }
        else return this;
      }
      default: return this;
    }
  }
}