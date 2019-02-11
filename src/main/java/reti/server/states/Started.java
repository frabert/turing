package reti.server.states;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.EphemeralServerState.ClientConnection;

/**
 * Lo stato iniziale di un client appena connesso
 */
public class Started extends ClientState {
  String username, password;
  Account account;

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    EphemeralServerState ephemeral = Server.state.getEphemeral();
    PersistentServerState persistent = Server.state.getPersistent();

    try {
      switch(msg.getType()) {
        // Il client chiede di effettuare il login
        case Message.TYPE_LOGIN_USER: {
          if(username != null)
            throw new InvalidMessageException();

          username = msg.getString();
          return this;
        }
        // Il client invia la password per il login
        case Message.TYPE_LOGIN_PASSWORD: {
          if(username == null || password != null)
            throw new InvalidMessageException();

          account = persistent.getAccount(username);

          if(ephemeral.connections.containsKey(account))
            throw new AlreadyLoggedException();
          
          password = msg.getString();

          if(!account.getPassword().equals(password))
            throw new InvalidPasswordException();
          
          conn.writer.addMessage(Message.TYPE_LOGIN_OK, 0);
          ephemeral.connections.put(account, conn);
          conn.account = account;
          return new LoggedIn();
        }
        default: {
          throw new NotLoggedInException();
        }
      }
    } catch(ServerErrorException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, ex.getErrorCode());
    }
    
    return new Started();
  }
}