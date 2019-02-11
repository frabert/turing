package reti.client.states;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import reti.*;
import reti.client.*;
import reti.client.states.common.*;

public class Editing extends ClientState {
  String documentOwner, documentName;
  int sectionIndex;
  Path filePath;

  public Editing(String documentOwner, String documentName, int sectionIndex,
                 Path filePath) {
    this.documentOwner = documentOwner;
    this.documentName = documentName;
    this.sectionIndex = sectionIndex;
    this.filePath = filePath;
  }

  @Override
  public ClientState handleMessage(Message msg) {
    switch(msg.getType()) {
      case Message.TYPE_INVITE_COUNT: {
        int count = msg.getInt();
        return new WaitingDocumentInvites(count, this);
      }
      default: return this;
    }
  }

  @Override
  public ClientState handleAction(String action) {
    switch (action) {
      case MainFrame.ACTION_ABORT_EDIT: {
        synchronized (Client.connection.writer) {
          Client.connection.writer.addMessage(Message.TYPE_END_EDIT_ABORT, 0);
        }
        Client.frame.setEditingMode(false);
        Client.chatFrame.setVisible(false);
        return new LoggedIn();
      }
      case MainFrame.ACTION_END_EDIT: {
        try {
          byte[] contents = Files.readAllBytes(filePath);

          synchronized (Client.connection.writer) {
            Client.connection.writer.addMessage(Message.TYPE_END_EDIT_SECTION, contents);
          }

          Client.frame.setEditingMode(false);
          Client.chatFrame.setVisible(false);
          return new LoggedIn();
        } catch (IOException e) {
          DialogUtilities.showErrorDialog("Impossibile leggere il file");
          return this;
        }
      }
      case MainFrame.ACTION_VIEW_DOCUMENT: {
        Document selectedDocument = Client.frame.getSelectedAvailableDocument();
        if(selectedDocument == null) return this;

        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(Client.frame);
        if(res != JFileChooser.APPROVE_OPTION) return this;
      
        synchronized(Client.connection.writer) {
          Client.connection.writer.addMessage(Message.TYPE_SHOW_DOCUMENT_OWNER, selectedDocument.getOwner());
          Client.connection.writer.addMessage(Message.TYPE_SHOW_DOCUMENT_NAME, selectedDocument.getName());
        }
        return new WaitingDocumentContents(chooser.getSelectedFile().getPath(), this);
      } 
      case  MainFrame.ACTION_VIEW_SECTION: {
        Document selectedDocument = Client.frame.getSelectedAvailableDocument();
        if(selectedDocument == null) return this;

        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(Client.frame);
        if(res != JFileChooser.APPROVE_OPTION) return this;

        Object[] options = new Object[selectedDocument.getSections()];
        for(int i = 0; i < options.length; i++) options[i] = i;

        Object section = JOptionPane.showInputDialog(
          Client.frame,
          "Selezionare la sezione da visualizzare",
          "Scelta sezione",
          JOptionPane.PLAIN_MESSAGE,
          null,
          options,
          options[0]);

        if(section == null) return this;

        int sectionNum = (Integer)section;
        synchronized(Client.connection.writer) {
          Client.connection.writer.addMessage(Message.TYPE_SHOW_SECTION_OWNER, selectedDocument.getOwner());
          Client.connection.writer.addMessage(Message.TYPE_SHOW_SECTION_NAME, selectedDocument.getName());
          Client.connection.writer.addMessage(Message.TYPE_SHOW_SECTION_IDX, sectionNum);
        }
        return new WaitingSectionContents(chooser.getSelectedFile().getPath(), this);
      }
      default: return this;
    }
  }
}