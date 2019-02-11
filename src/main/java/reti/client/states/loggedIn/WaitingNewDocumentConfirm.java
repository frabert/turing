package reti.client.states.loggedIn;

import javax.swing.SwingUtilities;

import reti.*;
import reti.client.*;
import reti.client.states.*;

/**
 * Stato del client che attende la conferma di creazione
 * di un nuovo documento dal server
 */
public class WaitingNewDocumentConfirm extends ClientState {
  String name;
  int sections;

  public WaitingNewDocumentConfirm(String name, int sections) {
    this.name = name;
    this.sections = sections;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_NEW_DOCUMENT_OK: {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            Client.frame.addOwnedDocument(name);
            Client.frame.addAvailableDocument(new Document(name, Client.connection.username, sections));
          }
        });
        return new LoggedIn();
      }
      case Message.TYPE_ERROR: {
        DialogUtilities.showErrorDialog("Impossibile creare il documento");
        return new LoggedIn();
      }
      default: return new LoggedIn();
    }
  }

}