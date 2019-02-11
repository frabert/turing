package reti;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Fornisce un'astrazione sopra ad un canale non bloccante che
 * consente di estrarre messaggi man mano che questi vengono
 * letti dal flusso TCP
 */
public class MessageReader {
  ByteBuffer buffer;

  public MessageReader() {
    this.buffer = ByteBuffer.allocate(1024);
  }

  Queue<Message> messages = new LinkedList<>();

  int msgLength = -1, msgType = -1, index = 0;
  byte[] contents;

  /**
   * Legge i messaggi dal buffer man mano che sono disponibili
   */
  void receiveMessage() {
    if(msgType < 0) {
      if(buffer.hasRemaining()) {
        msgType = buffer.getInt();
        receiveMessage();
      }
    } else if(msgLength < 0) {
      if(buffer.hasRemaining()) {
        msgLength = buffer.getInt();
        contents = new byte[msgLength];
        receiveMessage();
      }
    } else if(index <= msgLength) {
      while(buffer.hasRemaining() && index < msgLength) {
        contents[index++] = buffer.get();
      }

      if(index == msgLength) {
        messages.add(new Message(msgType, contents));
        msgLength = -1;
        msgType = -1;
        index = 0;
      }
    }
  }

  /**
   * Legge i messaggi disponibili sul canale
   * @param channel Il canale da cui leggere i messaggi
   * @throws IOException
   */
  public void read(SocketChannel channel)
      throws IOException
  {
    this.buffer.clear();
    channel.read(this.buffer);
    this.buffer.flip();
    while(this.buffer.hasRemaining()) {
      receiveMessage();
    }
  }

  /**
   * Restituisce il primo messaggio disponibile in coda
   * @return Il primo messaggio disponibile, o null
   */
  public Message getMessage() {
    return messages.poll();
  }
}