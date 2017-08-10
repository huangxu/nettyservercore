package com.youxigu.gs.message;

import com.youxigu.gs.core.Connection;

public class Message {
	public static final byte GAME_MESSAGE = 0;
	  public static final byte S2S_MESSAGE = 1;
	  public static final byte TYPE_GATE_KILL_CONNECTION = 31;
	  public static final byte USER_DISCONNECTED = 32;
	  public static final byte USER_CONNECTED = 33;
	  public static final byte HTTP_REQUEST = 34;
	  private Connection connection = null;

	  private byte[] data = null;

	  private byte msgType = 0;

	  private boolean pushed = false;

	  public boolean isPushed() {
	    return this.pushed;
	  }

	  public void setPushed(boolean pushed) {
	    this.pushed = pushed;
	  }

	  public byte getMsgType() {
	    return this.msgType;
	  }

	  public Message(Connection connection, byte type) {
	    this.connection = connection;
	    this.msgType = type;
	  }

	  public Message(Connection connection, byte[] data) {
	    this.connection = connection;
	    this.data = data;
	  }

	  public byte[] getData() {
	    return this.data;
	  }

	  public void setData(byte[] data) {
	    this.data = data;
	  }

	  public Connection getConnection() {
	    return this.connection;
	  }
}
