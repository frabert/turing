package reti.client;

import java.awt.HeadlessException;

import javax.swing.JOptionPane;

public class ChooseUserDialog {
  public Object chosenUser;

  public void showDialog(Object[] options) {
    try {
      chosenUser = JOptionPane.showInputDialog(
        Client.frame,
        "A quale utente inviare l'invito?",
        "Scelta utente",
        JOptionPane.QUESTION_MESSAGE,
        null,
        options, options[0]);
    } catch(HeadlessException ex){}
  }
}