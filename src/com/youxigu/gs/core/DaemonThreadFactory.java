package com.youxigu.gs.core;

import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory{
	 public static DaemonThreadFactory Singleton = new DaemonThreadFactory();

	  public Thread newThread(Runnable r)
	  {
	    Thread t = new Thread(r);
	    t.setDaemon(true);
	    return t;
	  }
}
