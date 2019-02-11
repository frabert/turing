package reti;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Fornisce un'astrazione sopra ad un canale non bloccante
 * che consente di inviare messaggi su uno stream TCP
 */
public class MessageWriter {
  ByteBuffer buffer;

  public MessageWriter() {
    this.buffer = ByteBuffer.allocate(1024);
    this.buffer.flip();
  }

  Queue<Message> messages = new LinkedList<>();

  boolean writtenType = false, writtenLength = false;
  int index = 0;
  Message m;

  /**
   * Mette i messaggi nel buffer man mano che si libera spazio
   */
  void writeMessage() {
    if(m != null) {
      if(!writtenType) {
        if(this.buffer.hasRemaining()) {
          this.buffer.putInt(m.getType());
          writtenType = true;
          writeMessage();
        }
      } else if(!writtenLength) {
        if(this.buffer.hasRemaining()) {
          this.buffer.putInt(m.getData().length);
          writtenLength = true;
          writeMessage();
        }
      } else if(index <= m.getData().length) {
        while(buffer.hasRemaining() && index < m.getData().length) {
          buffer.put(m.getData()[index++]);
        }

        if(index == m.getData().length) {
          writtenType = false;
          writtenLength = false;
          index = 0;
          m = null;
        }
      }
    }
  }

  /**
   * Invia i dati disponibili sul canale
   * @param channel Il canale su cui inviare i dati
   * @throws IOException
   */
  public void write(SocketChannel channel)
      throws IOException
  {
    if(this.buffer.hasRemaining()) {
      channel.write(this.buffer);
      if(this.buffer.hasRemaining()) return;
    }

    this.buffer.clear();
    while(this.buffer.hasRemaining() && (m = messages.poll()) != null) {
      writeMessage();
    }
    this.buffer.flip();
    channel.write(this.buffer);
  }

  /**
   * Aggiunge un messaggio alla coda di quelli da inviare
   * @param m Il messaggio da accodare
   */
  public void addMessage(Message m) {
    if(m == null) throw new NullPointerException();

    messages.add(m);
  }

  /**
   * Aggiunge un messaggio alla coda di quelli da inviare
   * @param type Il tipo del messaggio
   * @param msg  Il contenuto del messaggio
   */
  public void addMessage(int type, String msg) {
    if(msg == null) throw new NullPointerException();

    messages.add(new Message(type, msg));
  }

  /**
   * Aggiunge un messaggio alla coda di quelli da inviare
   * @param type Il tipo del messaggio
   * @param msg Il contenuto del messaggio
   */
  public void addMessage(int type, int msg) {
    messages.add(new Message(type, msg));
  }

  /**
   * Aggiunge un messaggio alla coda di quelli da inviare
   * @param type Il tipo del messaggio
   * @param msg Il contenuto del messaggio
   */
  public void addMessage(int type, byte[] msg) {
    if(msg == null) throw new NullPointerException();
    
    messages.add(new Message(type, msg));
  }

  /**
   * @return True se la code dei messaggi è vuota, false altrimenti
   */
  public boolean queueEmpty() {
    return messages.isEmpty() && m == null;
  }
}