package reti.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import reti.exceptions.*;

/**
 * Rappresenta una sezione di un documento
 */
public class DocumentSection implements java.io.Serializable {
  static final long serialVersionUID = 1L;

  Document mainDocument;
  Account lockingAccount;
  String contentsFile;

  public DocumentSection(Document mainDocument) throws IOException {
    this.mainDocument = mainDocument;
    this.contentsFile = RandomUtils.getRndFile();
  }

  /**
   * @return Il percorso del file che contiene i dati della sezione
   */
  public String getContentsFile() { return contentsFile; }

  /**
   * @return Il contenuto della sezione
   * @throws IOException Si è verificato un errore nella lettura del file
   */
  public byte[] readContents()
      throws IOException
  {
    ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
    Path fpath = Paths.get(contentsFile);

    FileChannel fchannel = FileChannel.open(fpath, StandardOpenOption.READ);
    ByteBuffer buf = ByteBuffer.allocate(1024);
    while(true) {
      int bytesRead = fchannel.read(buf);
      if(bytesRead < 0) break;
      buf.flip();
      bytestream.write(buf.array(), buf.arrayOffset(), buf.remaining());
      buf.clear();
    }
    fchannel.close();

    return bytestream.toByteArray();
  }

  /**
   * Segna la sezione come bloccata
   * @param account L'account che blocca la sezione
   * @throws SectionLockedException La sezione è già stata bloccata in precedenza e non è ancora stata sbloccata
   */
  public void Lock(Account account) throws SectionLockedException {
    if(lockingAccount != null)
      throw new SectionLockedException();

    lockingAccount = account;
  }

  /**
   * @return True se la sezione è bloccata, false altrimenti
   */
  public boolean isLocked() { return lockingAccount != null; }

  /**
   * Sblocca la sezione
   */
  public void Unlock()
  {
    lockingAccount = null;
  }

  /**
   * @return Il documento a cui appartiene questa sezione
   */
  public Document getMainDocument() { return mainDocument; }
}