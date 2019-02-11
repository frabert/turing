package reti.client;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

/**
 * Thread in background che ascolta i messaggi in arrivo
 * sulla chatroom in multicast
 */
public class ChatListener implements Runnable {
  InetAddress address;
  final ChatFrame frame;
  Hashtable<InetAddress, String> users = new Hashtable<>();

  public ChatListener(InetAddress address, ChatFrame frame) {
    this.address = address;
    this.frame = frame;
  }

  @Override
  public void run() {
    MulticastSocket listenSocket;
    try {
      listenSocket = new MulticastSocket(Client.chatPort);
      listenSocket.joinGroup(address);

      byte[] buf = new byte[1024];
      DatagramPacket dp = new DatagramPacket(buf, 1024);
      while(true) {
        listenSocket.receive(dp);
        final String msg = new String(
          dp.getData(),
          dp.getOffset(),
          dp.getLength(),
          StandardCharsets.UTF_8);
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            frame.addChatMessage(msg);
          }
        });
      }
    } catch (IOException e) {
      DialogUtilities.showErrorDialog(frame, "Errore di comunicazione");
    }
  }
}