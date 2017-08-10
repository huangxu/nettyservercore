package com.youxigu.gs.core;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;

public class HandlerExecutor {
	public static final int num = Runtime.getRuntime().availableProcessors()-1;
	private ExecutorService[] serviceArray = new ExecutorService[num];

	private static HandlerExecutor ins = new HandlerExecutor();

	public static HandlerExecutor ins() {
	   return ins;
	}

	private HandlerExecutor() {
	  for (int i = 0; i < num; i++){
		this.serviceArray[i] = Executors.newSingleThreadExecutor(new DefaultThreadFactory("handler executor thread"));
	  }
	}
	public void execute(int processId, Runnable task){
	   this.serviceArray[processId].submit(task);
	}
}
