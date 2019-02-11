package reti.server.states;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;
import reti.server.states.loggedIn.*;
import reti.server.states.common.*;

/**
 * Lo stato di un client che ha effettuato l'accesso
 */
public class LoggedIn extends ClientState {
  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    PersistentServerState persistent = Server.state.getPersistent();
    EphemeralServerState ephemeral = Server.state.getEphemeral();
    try {
      switch(msg.getType()) {
        // Il client richiede la disconnessione
        case Message.TYPE_LOGOUT: {
          ephemeral.connections.remove(conn.account);
          conn.account.onDisconnect();
          conn.account = null;
          conn.writer.addMessage(Message.TYPE_LOGOUT_OK, 0);
          return new Started();
        }
        // Il client richiede la lista dei documenti disponibili
        case Message.TYPE_LIST_DOCUMENTS: {
          Collection<Document> documents = conn.account.getAvailableDocuments();
          int count = documents.size();
          conn.writer.addMessage(Message.TYPE_DOCUMENT_LIST_COUNT, count);
          for(Document doc : documents) {
            conn.writer.addMessage(Message.TYPE_DOCUMENT_LIST_OWNER, doc.getOwner().getUsername());
            conn.writer.addMessage(Message.TYPE_DOCUMENT_LIST_NAME, doc.getName());
            conn.writer.addMessage(Message.TYPE_DOCUMENT_LIST_SECTIONS, doc.getSectionCount());
          }
          return this;
        }
        // Il client richiede la lista degli utenti registrati
        case Message.TYPE_LIST_USERS: {
          ArrayList<String> list = new ArrayList<>();
          for (Enumeration<String> e = persistent.getAccounts(); e.hasMoreElements();)
            list.add(e.nextElement());

          conn.writer.addMessage(Message.TYPE_USER_LIST_COUNT, list.size());
          for(String user : list) {
            conn.writer.addMessage(Message.TYPE_USER_LIST_NAME, user);
          }
          break;
        }
        // Il client richiede di modificare una sezione di un documento
        case Message.TYPE_EDIT_SECTION_OWNER: {
          String owner = msg.getString();
          return new EditSection(owner);
        }
        // Il client chiede di invitare un altro utente a modificare un suo documento
        case Message.TYPE_INVITE_USER_NAME: {
          String username = msg.getString();
          return new InviteUser(username);
        }
        // Il client chiede di visualizzare un documento
        case Message.TYPE_SHOW_DOCUMENT_OWNER: {
          String documentOwner = msg.getString();
          return new ShowDocument(documentOwner, this);
        }
        // Il client chiede di creare un nuovo documento
        case Message.TYPE_NEW_DOCUMENT_NAME: {
          String documentName = msg.getString();
          return new NewDocument(documentName);
        }
        // Il client chiede di visualizzare la sezione di un documento
        case Message.TYPE_SHOW_SECTION_OWNER: {
          String documentOwner = msg.getString();
          return new ShowSection(documentOwner, this);
        }
        default: throw new InvalidMessageException();
      }
    } catch(ServerErrorException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, ex.getErrorCode());
    }
    return new LoggedIn();
  }
}