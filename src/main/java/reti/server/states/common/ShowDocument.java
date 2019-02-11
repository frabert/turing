package reti.server.states.common;

import java.io.IOException;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;

/**
 * Lo stato di un client che sta effettuando la richiesta di visualizzazione di
 * un documento
 */
public class ShowDocument extends ClientState {
  String documentOwner;
  ClientState previous;

  public ShowDocument(String documentOwner, ClientState previous) {
    if(documentOwner == null) throw new NullPointerException();
    this.documentOwner = documentOwner;
    this.previous = previous;
  }

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    PersistentServerState persistent = Server.state.getPersistent();
    
    try {
      switch(msg.getType()) {
        case Message.TYPE_SHOW_DOCUMENT_NAME: {
          String documentName = msg.getString();

          Account owner = persistent.getAccount(documentOwner);
          Document doc = owner.getDocument(conn.account, documentName);

          if(!conn.account.getAvailableDocuments().contains(doc))
            throw new NotAllowedException();

          Pair<byte[], Boolean> contents = doc.getContents();

          conn.writer.addMessage(Message.TYPE_SHOW_DOCUMENT_OK, 0);
          conn.writer.addMessage(Message.TYPE_DOCUMENT_CONTENTS, contents.second ? 1 : 0);
          conn.writer.addMessage(Message.TYPE_DOCUMENT_CONTENTS, contents.first);
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

    return previous;
  }

  @Override
  public void handleDisconnection(ClientConnection conn) {
    previous.handleDisconnection(conn);
    super.handleDisconnection(conn);
  }

}