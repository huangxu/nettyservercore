package com.youxigu.gs.system;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterShutdown  implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(EnterShutdown.class);

	@Override
	public void run() {
		try {
			System.in.read();
			System.exit(0);
		} catch (IOException e) {
			logger.error("EnterShutdown error.......");
		}

	}
}
