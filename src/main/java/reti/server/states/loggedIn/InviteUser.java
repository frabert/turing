package reti.server.states.loggedIn;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;
import reti.server.states.*;

/**
 * Lo stato di un client che sta effettuando la richiesta di invito di un utente
 * ad un documento
 */
public class InviteUser extends ClientState {
  String username;

  public InviteUser(String username) {
    if(username == null) throw new NullPointerException();
    this.username = username;
  }

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    PersistentServerState persistent = Server.state.getPersistent();
    
    try {
      switch(msg.getType()) {
        case Message.TYPE_INVITE_USER_DOCUMENT: {
          String document = msg.getString();
          
          Document doc = conn.account.getDocument(conn.account, document);
          Account invitedUser = persistent.getAccount(username);
        
          if(invitedUser == conn.account)
            throw new InvalidUsernameException();

          doc.addAllowedAccount(invitedUser);
          invitedUser.addInvite(doc);

          conn.writer.addMessage(Message.TYPE_INVITE_USER_OK, 0);
          break;
        }
        default: {
          throw new InvalidMessageException();
        }
      }
    } catch(ServerErrorException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, ex.getErrorCode());
    }

    return new LoggedIn();
  }

}