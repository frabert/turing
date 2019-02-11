package reti.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import javax.swing.*;

/**
 * Finestra di chat
 */
public class ChatFrame
    extends JFrame implements ActionListener, WindowListener {
  private static final long serialVersionUID = 1L;

  DefaultListModel<String> chatModel;
  JList<String> chatMessages;

  JPanel defaultPanel;

  JButton sendMessage;
  JTextField messageField;

  Thread listenerThread;
  DatagramSocket socket;
  InetAddress address;

  public ChatFrame(InetAddress address) throws SocketException {
    super("Chat di gruppo");
    this.setVisible(true);
    this.setLocationRelativeTo(null);

    this.address = address;

    defaultPanel = new JPanel(new BorderLayout());
    this.setContentPane(defaultPanel);

    chatModel = new DefaultListModel<>();
    chatMessages = new JList<>(chatModel);
    chatMessages.setPreferredSize(new Dimension(400, 600));

    messageField = new JTextField(50);
    sendMessage = new JButton("Invia");
    sendMessage.setActionCommand("SEND");
    sendMessage.addActionListener(this);

    JPanel messagePanel = new JPanel(new BorderLayout());
    messagePanel.add(messageField, BorderLayout.CENTER);
    messagePanel.add(sendMessage, BorderLayout.LINE_END);

    add(chatMessages, BorderLayout.CENTER);
    add(messagePanel, BorderLayout.PAGE_END);

    socket = new DatagramSocket();

    listenerThread = new Thread(new ChatListener(address, this));
    listenerThread.start();

    pack();
  }

  /**
   * Elimina tutti i messaggi nella lista
   */
  public void clearChat() {
    chatModel.clear();
    chatMessages.setModel(chatModel);
  }

  /**
   * Aggiunge un messaggio alla lista
   * @param msg Il messaggio da inserire
   */
  public void addChatMessage(String msg) {
    chatModel.addElement(msg);
    chatMessages.setModel(chatModel);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String msg = messageField.getText();
    if (msg.length() > 0) {
      String packetContent =
          String.format("<%s>\t\t%s", Client.connection.username, msg);
      byte[] bytes = packetContent.getBytes(StandardCharsets.UTF_8);
      DatagramPacket packet =
          new DatagramPacket(bytes, bytes.length, address, Client.chatPort);
      try {
        socket.send(packet);
      } catch (IOException e1) {
      }
      messageField.setText("");
    }
  }

  @Override
  public void windowActivated(WindowEvent e) {}

  @Override
  public void windowClosed(WindowEvent e) {}

  @Override
  public void windowClosing(WindowEvent e) {
    listenerThread.interrupt();
    Client.frame.abortEdits();
  }

  @Override
  public void windowDeactivated(WindowEvent e) {}

  @Override
  public void windowDeiconified(WindowEvent e) {}

  @Override
  public void windowIconified(WindowEvent e) {}

  @Override
  public void windowOpened(WindowEvent e) {}
}