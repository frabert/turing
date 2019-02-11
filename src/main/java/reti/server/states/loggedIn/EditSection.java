package reti.server.states.loggedIn;

import java.io.IOException;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;
import reti.server.states.*;

/**
 * Lo stato di un client che sta effettuando la richiesta di modifica di una sezione
 */
public class EditSection extends ClientState {
  String documentOwner, documentName;

  public EditSection(String documentOwner) {
    if(documentOwner == null) throw new NullPointerException();
    this.documentOwner = documentOwner;
  }

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    PersistentServerState persistent = Server.state.getPersistent();
    
    try {
      switch(msg.getType()) {
        case Message.TYPE_EDIT_SECTION_DOCUMENT: {
          if(documentName != null)
            throw new InvalidMessageException();

          documentName = msg.getString();
          return this;
        }
        case Message.TYPE_EDIT_SECTION_IDX: {
          if(documentName == null)
            throw new InvalidMessageException();

          int sectionIndex = msg.getInt();

          Account acc = persistent.getAccount(documentOwner);
          Document doc = acc.getDocument(conn.account, documentName);
          DocumentSection section = doc.startEditSection(acc, sectionIndex);

          conn.writer.addMessage(Message.TYPE_EDIT_SECTION_OK, section.readContents());
          conn.writer.addMessage(Message.TYPE_DOCUMENT_CHAT_ADDR, doc.getChatAddress().getAddress());
          return new Editing(section);
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