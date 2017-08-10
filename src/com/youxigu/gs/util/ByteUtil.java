package com.youxigu.gs.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteUtil {
	public static final void setUbyte(byte[] data, int position, int value)
	  {
	    data[position] = (byte)(value & 0xFF);
	  }

	  public static final int getUbyte(byte[] data, int position) {
	    return data[position] & 0xFF;
	  }

	  public static final void setShort(byte[] data, int position, int value) {
	    data[position] = (byte)((value & 0xFF00) >> 8);
	    data[(position + 1)] = (byte)(value & 0xFF);
	  }

	  public static final short getShort(byte[] data, int position) {
	    return (short)(((data[position] & 0xFF) << 8) + (data[(position + 1)] & 0xFF));
	  }

	  public static final void setInt(byte[] data, int position, int value) {
	    data[position] = (byte)((value & 0xFF000000) >> 24);
	    data[(position + 1)] = (byte)((value & 0xFF0000) >> 16);
	    data[(position + 2)] = (byte)((value & 0xFF00) >> 8);
	    data[(position + 3)] = (byte)(value & 0xFF);
	  }

	  public static final int getInt(byte[] data, int position) {
	    return (data[position] << 24 & 0xFF000000) + (data[(position + 1)] << 16 & 0xFF0000) + (data[(position + 2)] << 8 & 0xFF00) + (data[(position + 3)] & 0xFF);
	  }

	  public static final void readAllData(InputStream in, byte[] buffer) throws IOException
	  {
	    int toRead = buffer.length;
	    int readed = 0;
	    while (readed < toRead)
	      readed += in.read(buffer, readed, toRead - readed);
	  }

	  public static final byte[] readAllData(InputStream in) throws IOException
	  {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int readed = 0;
	    while ((readed = in.read(buffer)) != -1) {
	      baos.write(buffer, 0, readed);
	    }
	    return baos.toByteArray();
	  }

	  public static final String byteArrayToHexString(byte[] b) {
	    return byteArrayToHexString(b, false);
	  }

	  public static final String byteArrayToHexString(byte[] b, boolean spaced) {
	    StringBuffer sb = new StringBuffer(b.length * 2);
	    for (int i = 0; i < b.length; i++) {
	      int v = b[i] & 0xFF;
	      if (v < 16) {
	        sb.append('0');
	      }
	      sb.append(Integer.toHexString(v));
	      if (spaced) {
	        sb.append(' ');
	      }
	    }
	    return sb.toString().toUpperCase();
	  }

	  public static final String getHexString(byte[] inByArr, int start, int len) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = start; i < start + len; i++) {
	      sb.append(Integer.toHexString(inByArr[i] & 0xFF)).append(' ');
	    }
	    return sb.toString();
	  }

	  public static final String byteArrayToPrintableString(byte[] b) {
	    StringBuffer sb = new StringBuffer(b.length);
	    for (int i = 0; i < b.length; i++) {
	      int v = b[i] & 0xFF;
	      if ((v < 32) || (v > 127))
	        sb.append(' ');
	      else {
	        sb.append((char)v);
	      }
	    }

	    return sb.toString();
	  }

	  public static final byte[] hexStringToByteArray(String s) throws NumberFormatException {
	    byte[] b = new byte[s.length() / 2];
	    for (int i = 0; i < b.length; i++) {
	      int index = i * 2;
	      int v = Integer.parseInt(s.substring(index, index + 2), 16);
	      b[i] = (byte)v;
	    }
	    return b;
	  }

	  public static final String intArrayToString(int[] array) {
	    if (array.length == 0) {
	      return "[]";
	    }
	    StringBuffer sb = new StringBuffer("[");
	    sb.append(array[0]);
	    for (int i = 1; i < array.length; i++) {
	      sb.append(", ");
	      sb.append(array[i]);
	    }
	    sb.append(']');
	    return sb.toString();
	  }

	  public static final byte[] getUTFBytes(String string) {
	    try {
	      return string.getBytes("UTF-8"); } catch (UnsupportedEncodingException e) {
	    }
	    return string.getBytes();
	  }

	  public static final String getUTFString(byte[] data)
	  {
	    return getUTFString(data, 0, data.length);
	  }

	  public static final String getUTFString(byte[] data, int offset, int length) {
	    try {
	      return new String(data, offset, length, "UTF-8"); } catch (UnsupportedEncodingException e) {
	    }
	    return new String(data);
	  }

	  public static final byte[] getBytes(byte[] source, int init)
	  {
	    int end = source.length;
	    return getBytes(source, init, end);
	  }

	  public static final byte[] getBytes(byte[] source, int init, int end) {
	    byte[] result = new byte[end - init];
	    System.arraycopy(source, init, result, 0, result.length);
	    return result;
	  }

	  public static final void writeUbyteUTFString(ByteBuffer buffer, String str)
	  {
	    if (str == null) {
	      buffer.put((byte) 0);
	    } else {
	      int currentPosition = buffer.position();
	      int length = str.length();
	      if (length > 255) {
	        length = 255;
	      }
	      buffer.put((byte)length);
	      try {
	        buffer.put(str.toUpperCase().getBytes("UTF-8"));
	      } catch (UnsupportedEncodingException e) {
	        buffer.put(currentPosition, (byte) 0);
	      }
	    }
	  }

	  public static final int writeUbyteUTFString(DataOutputStream dos, String string, int maxLength) throws IOException {
	    if (maxLength > 255) {
	      maxLength = 255;
	    }
	    byte[] stringBytes = getUTFBytes(string);
	    int toWrite = maxLength;
	    if (stringBytes.length <= maxLength) {
	      toWrite = stringBytes.length;
	    }
	    dos.write(toWrite);
	    dos.write(stringBytes, 0, toWrite);
	    return toWrite;
	  }

	  public static final String readUShortUTFString(DataInputStream dis) throws IOException {
	    byte[] buff = new byte[dis.readShort() & 0xFFFF];
	    int len = dis.read(buff);
	    return getUTFString(buff, 0, len);
	  }

	  public static final int writeUShortUTFString(DataOutputStream dos, String string)
	    throws IOException
	  {
	    return writeUShortUTFString(dos, string, 65536);
	  }

	  public static final int writeUShortUTFString(DataOutputStream dos, String string, int maxLength) throws IOException {
	    if (maxLength > 65536) {
	      maxLength = 65536;
	    }
	    int toWrite = maxLength;
	    byte[] stringBytes = getUTFBytes(string);
	    if (stringBytes.length <= maxLength) {
	      toWrite = stringBytes.length;
	    }
	    dos.writeShort(toWrite);
	    dos.write(stringBytes, 0, toWrite);

	    return toWrite;
	  }

	  public static final String getStackTrace(Throwable t) {
	    StackTraceElement[] elements = t.getStackTrace();
	    StringBuffer sb = new StringBuffer(new StringBuilder().append(t.getClass().getName()).append(" : ").append(t.getMessage()).toString());
	    for (int i = 0; i < elements.length; i++) {
	      StackTraceElement e = elements[i];
	      sb.append("\n\t");
	      sb.append(e.getClassName());
	      sb.append("::");
	      sb.append(e.getMethodName());
	      sb.append(" [");
	      sb.append(e.getLineNumber());
	      sb.append(']');
	    }
	    return sb.toString();
	  }

	  public static final boolean[] toBooleanArray(String string) {
	    boolean[] result = new boolean[string.length()];
	    for (int i = 0; i < string.length(); i++) {
	      result[i] = (boolean) (string.charAt(i) != '0' ? 1 : false);
	    }
	    return result;
	  }

	  public static final boolean[] toBooleanArray(String string, int numberOfFlags) {
	    boolean[] result = new boolean[numberOfFlags];
	    int i = 0;
	    for (; (i < string.length()) && (i < numberOfFlags); i++) {
	      result[i] = (boolean) (string.charAt(i) != '0' ? 1 : false);
	    }
	    for (; i < numberOfFlags; i++) {
	      result[i] = false;
	    }
	    return result;
	  }

//	  public static final int fromBinaryStringToInt(String string) {
//	    boolean[] binary = toBooleanArray(string);
//	    int result = 0;
//	    for (int i = 0; i < binary.length; i++) {
//	      result = (result << 1) + (binary[i] != 0 ? 1 : 0);
//	    }
//	    return result;
//	  }

	  public static final long getLong(byte[] data, int position) {
	    return (data[position] << 56) + ((data[(position + 1)] & 0xFF) << 48) + ((data[(position + 2)] & 0xFF) << 40) + ((data[(position + 3)] & 0xFF) << 32) + ((data[(position + 4)] & 0xFF) << 24) + ((data[(position + 5)] & 0xFF) << 16) + ((data[(position + 6)] & 0xFF) << 8) + ((data[(position + 7)] & 0xFF) << 0);
	  }

	  public static final void setLong(byte[] data, int position, long value)
	  {
	    data[(0 + position)] = (byte)(int)(value >> 56);
	    data[(1 + position)] = (byte)(int)(value >> 48);
	    data[(2 + position)] = (byte)(int)(value >> 40);
	    data[(3 + position)] = (byte)(int)(value >> 32);
	    data[(4 + position)] = (byte)(int)(value >> 24);
	    data[(5 + position)] = (byte)(int)(value >> 16);
	    data[(6 + position)] = (byte)(int)(value >> 8);
	    data[(7 + position)] = (byte)(int)(value >> 0);
	  }

	  public static final void setBytes(byte[] source, byte[] dest, int position, int length)
	  {
	    System.arraycopy(source, 0, dest, position, length);
	  }
}
