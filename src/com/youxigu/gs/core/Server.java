package com.youxigu.gs.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youxigu.gs.netty.NettyServer;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	private static final String VERSION = "2.0";
	public static final String ENCODING = "UTF-8";
	InetAddress localHost;
	public static final int STATUS_DEFAULT = 0;
	public static final int STATUS_STARTING = 1;
	public static final int STATUS_RUNNING = 2;
	public static final int STATUS_STOPPING = 3;
	public static final int STATUS_STOPPED = 4;
	private int status = 0;
	private NettyServer nettyServer ;
	protected boolean running = false;
	protected boolean failed = false;
	protected boolean finished = false;

	protected Service service = null;

	public void start() {
		logger.info("Running Youxigu game server, control thread started");
		this.finished = false;
		try {
			runInternal();
		} catch (Exception e) {
			logger.error("Error executing Youxigu game server", e);
			stopServer();
		}
		this.finished = true;
		this.status = 4;
		logger.info("Youxigu game server control thread stopped");
	}

	private void runInternal() {
		boolean failed = !startServer();
		if (failed) {
			stopServer();
			return;
		}
		this.running = true;
		this.status = 2;
		while (this.running) {
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		stopServer();
	}

	private boolean startServer() {
		logger.info("Starting up Youxigu game server...");
		logger.info("Step #1: Acquiring configuration for this server...");

		if (!acquireServerConfiguration()) {
			return false;
		}

		logger.info("Step 2: Checking port...");
		if (!isPortAvaliable()) {
			return false;
		}
		logger.info("Step #3: Preparing services...");
		if (!instanceServices()) {
			return false;
		}

		if (!checkServices()) {
			return false;
		}

		logger.info("Step #4: Starting services...");
		if (!startService()) {
			return false;
		}
		logger.info("Step #5: Starting netty server...");
		nettyServer = new NettyServer(service);
		try {
			this.status = 2 ;
			nettyServer.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean isPortAvaliable() {
		boolean bRet = false;
		ServerSocketChannel serverChannel = null;
		try {
			serverChannel = ServerSocketChannel.open();
			InetSocketAddress iddr = null;
			if (Configuration.getInstance().getClientBindAddress() == null) {
				iddr = new InetSocketAddress(Configuration.getInstance()
						.getClientPort());
			} else {
				iddr = new InetSocketAddress(Configuration.getInstance()
						.getClientBindAddress(), Configuration.getInstance()
						.getClientPort());
			}
			serverChannel.socket().bind(iddr);
			serverChannel.configureBlocking(false);

			if (serverChannel != null) {
				serverChannel.close();
			}
			bRet = true;
		} catch (Exception e) {
			bRet = false;
			logger.error("", e);
		} finally {
			try {
				if (serverChannel != null)
					serverChannel.close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return bRet;
	}

	private boolean checkServices() {
		return this.service != null;
	}

	private boolean startService() {
		if (this.service == null) {
			logger.warn("There aren't services defined for this server!");
			return false;
		}
		try {
			if ((!this.service.isRunning()) && (!this.service.init())) {
				logger.error("Could not start services. Status is: "
						+ this.service.isRunning());
				if (this.service.isRunning())
					this.service.end();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			logger.error(
					"Could not start services. Status is: "
							+ this.service.isRunning(), e);
			return false;
		}

		return true;
	}

	private void stopService() {
		if ((this.service != null) && (this.service.isRunning()))
			this.service.end();
	}

	public Service getService() {
		return this.service;
	}

	private boolean instanceServices() {
		String serviceName = Configuration.getInstance().getServiceName();
		if (serviceName == null) {
			logger.error("No services are defined for this server");
			return false;
		}
		try {
			Object o = Class.forName(serviceName).newInstance();
			if ((o instanceof Service)) {
				this.service = ((Service) o);
			} else {
				logger.error("Class "
						+ this.service
						+ " is not a proper service (it does not implement 'ServiceInterface'");

				return false;
			}
		} catch (Exception e) {
			logger.warn("Can't instantiate " + this.service + ": "
					+ e.getClass().getCanonicalName());
			if (logger.isDebugEnabled()) {
				logger.debug("Cant instantiate " + this.service, e);
			}
		}

		return true;
	}

	private void stopServer() {
		logger.info("Shutting down Youxigu game server...");

		logger.info("Step #1: Stopping services...");
		stopService();
		this.finished = true;
		logger.info("Step #2: Closing gates...");
	}

	public boolean acquireLocalhost() {
		try {
			this.localHost = InetAddress.getLocalHost();
			return true;
		} catch (UnknownHostException e) {
			logger.error("Can't get localhost address {}", e);
		}
		return false;
	}

	public boolean acquireServerConfiguration() {
		if (Configuration.getInstance().getServerIP() == null) {
			Configuration.getInstance().setServerIP("127.0.0.1");
		}
		logger.info("Server IP is " + Configuration.getInstance().getServerIP());

		return true;
	}

	public boolean init() {
		this.status = 1;
		logger.info("Init Youxigu game server v"+VERSION);
		Runtime.getRuntime().addShutdownHook(new Shutdown());
		start();
		return true;
	}

	public void end() {
		if (this.status == 2) {
			logger.info("Ending Youxigu game server v"+VERSION);
			this.status = 3;
			this.running = false;
			synchronized (this) {
				notifyAll();
			}
			this.nettyServer.closeServer();
			this.stopServer();
		} else {
			logger.warn("You can't stop a server that is not running!");
		}
	}

	public boolean haveFailed() {
		return this.failed;
	}

	public boolean isRunning() {
		return this.running;
	}

	private void killServer() {
		logger.info("Starting kill server...");
		end();
		while (!this.finished)
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			}
		logger.info("Kill server done!");
	}

	public int getStatus() {
		return this.status;
	}

	public class Shutdown extends Thread {
		public Shutdown() {
		}

		public void run() {
			Server.this.killServer();
		}
	}
}
