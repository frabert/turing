package reti.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

import reti.*;
import reti.server.EphemeralServerState.ClientConnection;

public class Server 
{
  public static int registrationPort = 1080;
  public static int serverPort = 1081;

  public static ServerState state;
  
  static void parseArgs(String[] args) {
    for(int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "--regPort":
        case "-r":
        {
          try {
            registrationPort = Integer.parseInt(args[++i]);
          } catch(IndexOutOfBoundsException ex) {
            System.err.println("Formato corretto: --regPort <porta>");
            System.exit(1);
          } catch(NumberFormatException ex) {
            System.err.println("La porta deve essere un intero");
            System.exit(1);
          }
          break;
        }
        case "--servPort":
        case "-s":
        {
          try {
            serverPort = Integer.parseInt(args[++i]);
          } catch(IndexOutOfBoundsException ex) {
            System.err.println("Formato corretto: --servPort <porta>");
            System.exit(1);
          } catch(NumberFormatException ex) {
            System.err.println("La porta deve essere un intero");
            System.exit(1);
          }
          break;
        }
        case "--load":
        case "-l":
        {
          try {
            FileInputStream stream = new FileInputStream(args[++i]);
            state = new ServerState(stream);
          } catch(IndexOutOfBoundsException ex) {
            System.err.println("Formato corretto: --load <file>");
            System.exit(1);
          } catch(FileNotFoundException ex) {
            System.err.println("Impossibile caricare il file");
            System.exit(1);
          } catch(IOException ex) {
            System.err.println("Impossibile caricare il file");
            System.exit(1);
          } catch(ClassNotFoundException ex) {
            System.err.println("Impossibile caricare il file");
            System.exit(1);
          }
          break;
        }
        case "-h":
        case "--help": {
          System.out.println("java reti.server.Server [--regPort/-r <porta>] [--servPort/-s <porta>] [--load/-l <file>]");
          System.out.println("   --regPort/-r: Specifica la porta su cui attivare il servizio di registrazione");
          System.out.println("  --servPort/-s: Specifica la porta su cui attivare il server principale");
          System.out.println("      --load/-l: Carica uno stato salvato in precedenza");
          System.exit(0);
          break;
        }
        default: {
          System.err.printf("Opzione non riconosciuta: %s\n", args[i]);
          System.exit(1);
          break;
        }
      }
    }
  }

  public static void main( String[] args ) {
    state = new ServerState();

    parseArgs(args);

    // Registra un hook per salvare lo stato al momento della terminazione
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          FileOutputStream stream = new FileOutputStream("state.dat");
          state.save(stream);
        } catch (Exception e) { }
      }
    }));

    try {
      RegistrationServer registrationServer = new RegistrationServer(state);
      LocateRegistry.createRegistry(registrationPort);
      Registry registry = LocateRegistry.getRegistry(registrationPort);

      registry.bind("TURING-REGISTER", registrationServer);
    } catch(RemoteException ex) {
      System.err.println("Errore di comunicazione");
      ex.printStackTrace();
      return;
    } catch(AlreadyBoundException ex) {
      System.err.println("Servizio di registrazione già in esecuzione");
      return;
    }

    ServerSocketChannel serverChannel;
    Selector selector;
    SelectionKey selKey;
    
    try {
      serverChannel = ServerSocketChannel.open();
      serverChannel.socket().bind(new InetSocketAddress(serverPort));
      serverChannel.configureBlocking(false);
      
      selector = Selector.open();
      selKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT, null);
    } catch(IOException ex) {
      ex.printStackTrace();
      return;
    }

    System.out.println("Server in ascolto");
    while(true) {
      Set<SelectionKey> key_set;
      Iterator<SelectionKey> key_it;

      try {
        int keys_no = selector.select();
        key_set = selector.selectedKeys();
        key_it = key_set.iterator();
        
        selKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT, null);
      } catch(IOException ex) {
        ex.printStackTrace();
        return;
      }

      while(key_it.hasNext()) {
        SelectionKey key = key_it.next();
        
        if(key.isAcceptable()) {
          try {
            SocketChannel clientChannel = serverChannel.accept();
            if(clientChannel == null) continue;
            clientChannel.configureBlocking(false);
            ClientConnection conn = state.ephemeral.new ClientConnection();
            clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, conn);
          } catch(IOException ex) {

          }
        } else if(key.isReadable()) {
          SocketChannel clientChannel = (SocketChannel)key.channel();
          ClientConnection conn = (ClientConnection)key.attachment();
          try {
            conn.reader.read(clientChannel);
            Message msg;
            
            while((msg = conn.reader.getMessage()) != null) {
              conn.state = conn.state.handleMessage(msg, conn);
            }
          } catch(IOException ex) {
            conn.state.handleDisconnection(conn);
            if(conn.account != null) {
              state.getEphemeral().connections.remove(conn.account);
            }
          }
        } else if(key.isWritable()) {
          SocketChannel clientChannel = (SocketChannel)key.channel();
          ClientConnection conn = (ClientConnection)key.attachment();
          try {
            if(conn.account != null) {
              // Invia all'utente tutti gli inviti arrivati non appena è possibile
              Collection<Document> invites = conn.account.getInvites();
              if(invites.size() > 0) {
                conn.writer.addMessage(Message.TYPE_INVITE_COUNT, invites.size());
                for(Document doc : invites) {
                  conn.writer.addMessage(Message.TYPE_INVITE_USER_NAME, doc.getOwner().getUsername());
                  conn.writer.addMessage(Message.TYPE_INVITE_USER_DOCUMENT, doc.getName());
                  conn.writer.addMessage(Message.TYPE_INVITE_USER_SECTIONS, doc.getSectionCount());
                }
                conn.account.clearInvites();
              }
            }

            conn.writer.write(clientChannel);
          } catch(IOException ex) {
            conn.state.handleDisconnection(conn);
            if(conn.account != null) {
              state.getEphemeral().connections.remove(conn.account);
            }
          }
        }
        key_it.remove();
      }
    }
  }
}
