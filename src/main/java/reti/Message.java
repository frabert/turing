package reti;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Rappresenta un messaggio scambiato fra client e server.
 * 
 * Viene codificato come un messaggio Type-Length-Value
 */
public class Message
    implements Serializable
{
  static final long serialVersionUID = 1L;

  public static final int TYPE_LOGIN_USER            = 0x00;
  public static final int TYPE_LOGIN_PASSWORD        = 0x01;
  public static final int TYPE_LOGIN_OK              = 0x02;

  public static final int TYPE_LOGOUT                = 0x10;
  public static final int TYPE_LOGOUT_OK             = 0x11;

  public static final int TYPE_NEW_DOCUMENT_NAME     = 0x20;
  public static final int TYPE_NEW_DOCUMENT_SECTIONS = 0x21;
  public static final int TYPE_NEW_DOCUMENT_OK       = 0x22;

  public static final int TYPE_INVITE_COUNT          = 0x30;
  public static final int TYPE_INVITE_USER_NAME      = 0x31;
  public static final int TYPE_INVITE_USER_DOCUMENT  = 0x32;
  public static final int TYPE_INVITE_USER_SECTIONS  = 0x33;
  public static final int TYPE_INVITE_USER_OK        = 0x34;

  public static final int TYPE_EDIT_SECTION_OWNER    = 0x40;
  public static final int TYPE_EDIT_SECTION_DOCUMENT = 0x41;
  public static final int TYPE_EDIT_SECTION_IDX      = 0x42;
  public static final int TYPE_EDIT_SECTION_OK       = 0x43;

  public static final int TYPE_END_EDIT_SECTION      = 0x50;
  public static final int TYPE_END_EDIT_ABORT        = 0x51;
  public static final int TYPE_END_EDIT_OK           = 0x52;

  public static final int TYPE_LIST_DOCUMENTS        = 0x60;

  public static final int TYPE_SECTION_CONTENTS      = 0x70;

  public static final int TYPE_ERROR                 = 0x80;

  public static final int TYPE_DOCUMENT_LIST_COUNT   = 0x90;
  public static final int TYPE_DOCUMENT_LIST_OWNER   = 0x91;
  public static final int TYPE_DOCUMENT_LIST_NAME    = 0x92;
  public static final int TYPE_DOCUMENT_LIST_SECTIONS= 0x93;

  public static final int TYPE_SHOW_DOCUMENT_OWNER   = 0xA0;
  public static final int TYPE_SHOW_DOCUMENT_NAME    = 0xA1;
  public static final int TYPE_SHOW_DOCUMENT_OK      = 0xA2;

  public static final int TYPE_DOCUMENT_CONTENTS     = 0xB0;
  
  public static final int TYPE_SHOW_SECTION_OWNER    = 0xC0;
  public static final int TYPE_SHOW_SECTION_NAME     = 0xC1;
  public static final int TYPE_SHOW_SECTION_IDX      = 0xC2;
  public static final int TYPE_SHOW_SECTION_OK       = 0xC3;
  public static final int TYPE_SHOW_SECTION_CONTENTS = 0xC4;

  public static final int TYPE_DOCUMENT_CHAT_ADDR    = 0xD0;

  public static final int TYPE_LIST_USERS            = 0xE0;
  public static final int TYPE_USER_LIST_COUNT       = 0xE1;
  public static final int TYPE_USER_LIST_NAME        = 0xE2;

  public static final int ERROR_GENERIC              = 0x00;
  public static final int ERROR_INVALID_USER         = 0x01;
  public static final int ERROR_INVALID_PASSWORD     = 0x02;
  public static final int ERROR_ALREADY_LOGGED       = 0x03;
  public static final int ERROR_NOT_LOGGED           = 0x04;
  public static final int ERROR_NOT_ALLOWED          = 0x05;
  public static final int ERROR_INVALID_DOCUMENT     = 0x06;
  public static final int ERROR_SECTION_LOCKED       = 0x07;
  public static final int ERROR_INVALID_MESSAGE      = 0x08;
  public static final int ERROR_INVALID_SECTION      = 0x09;

  int type;
  byte[] data;

  public Message(int type, byte[] data) {
    if(data == null) throw new NullPointerException();

    this.type = type;
    this.data = data;
  }

  public Message(int type, String data) {
    if(data == null) throw new NullPointerException();

    this.type = type;
    this.data = data.getBytes(StandardCharsets.UTF_8);
  }

  public Message(int type, int data) {
    this.type = type;
    ByteBuffer buf = ByteBuffer.allocate(4);
    buf.putInt(data);
    this.data = buf.array();
  }

  public int getType() { return type; }
  public byte[] getData() { return data; }

  public String getString() {
    return new String(data, StandardCharsets.UTF_8);
  }

  public int getInt() {
    ByteBuffer buf = ByteBuffer.wrap(data);
    return buf.getInt();
  }
}