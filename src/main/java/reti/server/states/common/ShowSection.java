package reti.server.states.common;

import java.io.IOException;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;

/**
 * Lo stato di un client che sta effettuando la richiesta di visualizzazione
 * di una sezione
 */
public class ShowSection extends ClientState {
  String documentOwner, documentName;
  ClientState previous;

  public ShowSection(String documentOwner, ClientState previous) {
    if(documentOwner == null) throw new NullPointerException();
    this.documentOwner = documentOwner;
    this.previous = previous;
  }

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    PersistentServerState persistent = Server.state.getPersistent();
    
    try {
      switch(msg.getType()) {
        case Message.TYPE_SHOW_SECTION_NAME: {
          if(documentName != null)
            throw new InvalidMessageException();

          documentName = msg.getString();
          return this;
        }
        case Message.TYPE_SHOW_SECTION_IDX: {
          if(documentName == null)
            throw new InvalidMessageException();

          int sectionIndex = msg.getInt();

          Account acc = persistent.getAccount(documentOwner);
          Document doc = acc.getDocument(conn.account, documentName);

          if(!conn.account.getAvailableDocuments().contains(doc))
            throw new NotAllowedException();

          if(sectionIndex >= doc.getSectionCount())
            throw new InvalidSectionException();
          
          DocumentSection section = doc.getSection(conn.account, sectionIndex);

          conn.writer.addMessage(Message.TYPE_SHOW_SECTION_OK, section.isLocked() ? 1 : 0);
          conn.writer.addMessage(Message.TYPE_SHOW_SECTION_CONTENTS, section.readContents());
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