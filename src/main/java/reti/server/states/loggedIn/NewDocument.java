package reti.server.states.loggedIn;

import java.io.IOException;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;
import reti.server.states.*;

/**
 * Lo stato di un client che sta effettuando la richiesta di creazione di un
 * nuovo documento
 */
public class NewDocument extends ClientState {
  String documentName;

  public NewDocument(String documentName) {
    if(documentName == null) throw new NullPointerException();
    this.documentName = documentName;
  }

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    try {
      switch(msg.getType()) {
        case Message.TYPE_NEW_DOCUMENT_SECTIONS: {
          int documentSections = msg.getInt();

          if(documentSections <= 0)
            throw new InvalidMessageException();

          conn.account.createDocument(documentName, documentSections);
          conn.writer.addMessage(Message.TYPE_NEW_DOCUMENT_OK, 0);
          break;
        }
        default: {
          throw new InvalidMessageException();
        }
      }
    } catch(ServerErrorException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, ex.getErrorCode());
    } catch(IOException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, Message.ERROR_GENERIC);
    }

    return new LoggedIn();
  }

}