package com.youxigu.gs.util;

import java.net.InetAddress;

import io.netty.util.AttributeKey;

public class NettyUtil {
	public static AttributeKey<Integer> sessionId = AttributeKey.valueOf("sessionId");
	public static AttributeKey<Object> player = AttributeKey.valueOf("player");
	public static final String inetAddressToIPString(InetAddress inet){
	    StringBuffer sb = new StringBuffer();
	    sb.append(inet.getAddress()[0] & 0xFF);
	    sb.append('.');
	    sb.append(inet.getAddress()[1] & 0xFF);
	    sb.append('.');
	    sb.append(inet.getAddress()[2] & 0xFF);
	    sb.append('.');
	    sb.append(inet.getAddress()[3] & 0xFF);
	    return sb.toString();
	 }
	 public static final boolean pingHost(String host, int timeout){
	    try {
	      InetAddress hostAddr = InetAddress.getByName(host);
	      return hostAddr.isReachable(timeout);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return false;
	  }
	  public static final boolean pingHost(String host) {
	    return pingHost(host, 10000);
	  }
}
