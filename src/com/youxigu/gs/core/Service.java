package com.youxigu.gs.core;

import com.youxigu.gs.message.Message;

public interface Service {
	public abstract String getServiceName();
	public abstract void processDown(Message paramMessage);
	public abstract boolean isRunning();
	public abstract boolean haveFailed();
	public abstract boolean init();
	public abstract void end();
}
