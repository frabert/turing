package reti.client;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Funzioni d'utilità per visualizzare finestre di dialogo
 * da thread diversi da quello del dispatch dei messaggi Swing
 */
public class DialogUtilities {
  public static void showInfoDialog(final String message) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        JOptionPane.showMessageDialog(Client.frame, message); 
      }
    });
  }

  public static void showErrorDialog(final String message) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        JOptionPane.showMessageDialog(
          Client.frame,
          message,
          "Errore",
          JOptionPane.ERROR_MESSAGE); 
      }
    });
  }

  public static void showErrorDialogAndExit(final String message) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        JOptionPane.showMessageDialog(
          Client.frame,
          message,
          "Errore",
          JOptionPane.ERROR_MESSAGE); 
        System.exit(1);
      }
    });
  }

  public static void showInfoDialog(final Component parent, final String message) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        JOptionPane.showMessageDialog(parent, message); 
      }
    });
  }

  public static void showErrorDialog(final Component parent, final String message) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        JOptionPane.showMessageDialog(
          parent,
          message,
          "Errore",
          JOptionPane.ERROR_MESSAGE); 
      }
    });
  }
}