package reti.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Varie funzioni d'utilità per la generazione di dati casuali
 */
public class RandomUtils {
  /**
   * Genera una stringa alfanumerica casuale
   * @param length Lunghezza della stringa da generare
   * @return Una stringa casuale
   */
  public static String genRndString(int length) {
    String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    Random rnd = new Random();
    char[] chars = new char[length];
    for(int i = 0; i < length; i++) {
      chars[i] = pool.charAt(rnd.nextInt(pool.length()));
    }

    return new String(chars);
  }

  /**
   * Crea un file dal nome casuale
   * @return Il percorso di un file casuale
   * @throws IOException Non è stato possibile creare il file
   */
  public static String getRndFile() throws IOException {
    String fileName = genRndString(10);
    int len = 10;
    File file = new File(fileName + ".txt");
    while(!file.createNewFile()) {
      fileName = genRndString(++len);
      file = new File(fileName + ".txt");
    }
    return fileName + ".txt";
  }

  /**
   * @return Un indirizzo multicast casuale 
   */
  public static InetAddress genRndMulticastAddress() {
    EphemeralServerState ephemeral = Server.state.getEphemeral();
    Random rnd = new Random();
    while(true) {
      byte b = (byte)(rnd.nextInt(255) + 1);
      try {
        InetAddress addr = InetAddress.getByAddress(new byte[] { (byte)224, 0, 0, b });
        if(addr.isMulticastAddress() && !ephemeral.generatedAddresses.contains(addr)) {
          ephemeral.generatedAddresses.add(addr);
          return addr;
        }
      } catch(UnknownHostException ex) { }
    }
  }
}