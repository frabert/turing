package reti.client.states.loggedIn;

import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;
import reti.client.states.*;

/**
 * Stato del client che attende la lista dei documenti
 * disponibili dal server
 */
public class DocumentList extends ClientState {
  int received = 0;
  int count;

  String owner, name;

  public DocumentList(int count) {
    this.count = count;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_DOCUMENT_LIST_OWNER: {
        owner = msg.getString();
        return this;
      }
      case Message.TYPE_DOCUMENT_LIST_NAME: {
        name = msg.getString();
        return this;
      }
      case Message.TYPE_DOCUMENT_LIST_SECTIONS: {
        int sectionCount = msg.getInt();
        received++;

        final Document doc = new Document(name, owner, sectionCount);
        try {
          if(owner.equals(Client.connection.username)) {
            SwingUtilities.invokeAndWait(new Runnable() {
              @Override
              public void run() {
                Client.frame.addOwnedDocument(name);
              }
            });
          }
        
          SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
              Client.frame.addAvailableDocument(doc);
            }
          });
        } catch(Exception ex) {}

        if(received == count) return new LoggedIn();
        else {
          owner = null;
          name = null;
          return this;
        }
      }
      default: return this;
    }
  }
}