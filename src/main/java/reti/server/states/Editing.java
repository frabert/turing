package reti.server.states;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import reti.*;
import reti.exceptions.*;
import reti.server.*;
import reti.server.states.common.*;
import reti.server.EphemeralServerState.ClientConnection;

/**
 * Lo stato di un client che ha effettuato l'accesso e ha richiesto
 * di modificare una sezione di un documento
 */
public class Editing extends ClientState {
  DocumentSection section;

  public Editing(DocumentSection section) {
    if(section == null) throw new NullPointerException();
    this.section = section;
  }

  @Override
  public ClientState handleMessage(Message msg, ClientConnection conn) {
    try {
      switch(msg.getType()) {
        // Il client invia le modifiche effettuate alla sezione
        case Message.TYPE_END_EDIT_SECTION: {
          byte[] data = msg.getData();

          Path fpath = Paths.get(section.getContentsFile());
          FileChannel fchannel = FileChannel.open(fpath,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);

          ByteBuffer buf = ByteBuffer.wrap(data);
          while(buf.hasRemaining()) {
            fchannel.write(buf);
          }
          fchannel.close();

          conn.writer.addMessage(Message.TYPE_END_EDIT_OK, 0);

          section.getMainDocument().endEditingSection(conn.account, section);
          return new LoggedIn();
        }
        case Message.TYPE_END_EDIT_ABORT: {
          conn.writer.addMessage(Message.TYPE_END_EDIT_OK, 0);
          section.getMainDocument().endEditingSection(conn.account, section);
          return new LoggedIn();
        }
        // Il client chiede di visualizzare un documento
        case Message.TYPE_SHOW_DOCUMENT_OWNER: {
          String documentOwner = msg.getString();
          return new ShowDocument(documentOwner, this);
        }
        // Il client chiede di visualizzare la sezione di un documento
        case Message.TYPE_SHOW_SECTION_OWNER: {
          String documentOwner = msg.getString();
          return new ShowSection(documentOwner, this);
        }
        default: {
          throw new InvalidMessageException();
        }
      }
    } catch(IOException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, Message.ERROR_GENERIC);
    } catch(ServerErrorException ex) {
      conn.writer.addMessage(Message.TYPE_ERROR, ex.getErrorCode());
    }

    return new Editing(section);
  }

  @Override
  public void handleDisconnection(ClientConnection conn) {
    section.getMainDocument().endEditingSection(conn.account, section);
    super.handleDisconnection(conn);
  }
}