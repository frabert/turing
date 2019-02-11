package reti.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import javax.swing.*;

import reti.*;

public class Client {
  public static String host = "localhost";
  public static int registrationPort = 1080;
  public static int serverPort = 1081;
  public static int chatPort = 1082;

  public static MainFrame frame;
  public static ChatFrame chatFrame;
  public static Connection connection;
  
  static void parseArgs(String[] args) {
    for(int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "--host":
        case "-n":
        {
          try {
            host = args[++i];
          } catch(IndexOutOfBoundsException ex) {
            System.err.println("Formato corretto: --host <hostname>");
            System.exit(1);
          }
          break;
        }
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
        case "--chatPort":
        case "-c":
        {
          try {
            chatPort = Integer.parseInt(args[++i]);
          } catch(IndexOutOfBoundsException ex) {
            System.err.println("Formato corretto: --chatPort <porta>");
            System.exit(1);
          } catch(NumberFormatException ex) {
            System.err.println("La porta deve essere un intero");
            System.exit(1);
          }
          break;
        }
        case "-h":
        case "--help": {
          System.out.println("java reti.client.Client [--host/-n <hostname>] [--regPort/-r <porta>] [--servPort/-s <porta>] [--chatPort/-c <porta>]");
          System.out.println("      --host/-n: Specifica l'host a cui connettersi");
          System.out.println("   --regPort/-r: Specifica la porta su cui è attivo il servizio di registrazione");
          System.out.println("  --servPort/-s: Specifica la porta su cui è attivo il server principale");
          System.out.println("  --chatPort/-c: Specifica la porta su cui inviare e ricevere messaggi chat");
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

  /**
   * Tenta di effettuare il login, visualizzando la finestra di dialogo
   * all'utente.
   * 
   * Se l'utente chiude la finestra senza inserire dati, l'applicazione termina.
   */
  public static void AttemptLogin() {
    LoginDialog loginDialog = new LoginDialog(frame);
    loginDialog.setLocationRelativeTo(frame);
    loginDialog.setVisible(true);

    String username = loginDialog.getUsername();
    String password = loginDialog.getPassword();
    if(username == null || password == null) System.exit(0);

    connection.username = username;

    synchronized(connection.writer) {
      connection.writer.addMessage(Message.TYPE_LOGIN_USER, username);
      connection.writer.addMessage(Message.TYPE_LOGIN_PASSWORD, password);
    }
  }

  public static void main(String[] args) {
    parseArgs(args);

    frame = new MainFrame();

    try {
      InetSocketAddress addr = new InetSocketAddress(host, serverPort);
      SocketChannel channel = SocketChannel.open(addr);
      channel.configureBlocking(false);

      connection = new Connection();
      connection.channel = channel;
      connection.reader = new MessageReader();
      connection.writer = new MessageWriter();

      Thread tcpThread = new Thread(new ServerConnection());
      tcpThread.start();

      AttemptLogin();
    } catch(IOException ex) {
      JOptionPane.showMessageDialog(
        frame,
        "Errore di comunicazione con il server",
        "Errore",
        JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }
}