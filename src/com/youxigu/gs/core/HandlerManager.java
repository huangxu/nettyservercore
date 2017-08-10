package com.youxigu.gs.core;

import java.util.HashMap;
import java.util.Map;

public class HandlerManager {
	public Handler[] Handlers = null;

	  private Map<Short, Handler> restricHandlers = new HashMap<>();

	  public static final HandlerManager Singleton = new HandlerManager();

	  public void setupHandler(int size)
	  {
	    if (this.Handlers == null)
	      this.Handlers = new Handler[size];
	  }

	  public boolean openHandler(short type)
	  {
	    Handler handler = (Handler)this.restricHandlers.get(Short.valueOf(type));
	    if (handler != null) {
	      this.Handlers[type] = handler;
	      this.restricHandlers.remove(Short.valueOf(type));
	      return true;
	    }
	    return false;
	  }

	  public boolean closeHandler(short type)
	  {
	    Handler handler = this.Handlers[type];
	    if (handler != null) {
	      this.restricHandlers.put(Short.valueOf(type), handler);
	      this.Handlers[type] = null;
	      return true;
	    }
	    return false;
	  }
}
