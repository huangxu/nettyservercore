package com.youxigu.gs.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;

import com.youxigu.gs.netty.HandlerDispatcher;
import com.youxigu.gs.util.ByteUtil;
import com.youxigu.gs.util.CfgToolbox;

public class Configuration {
	private String prefix = "";
	  private String serverIP;
	  private InetAddress clientBindAddress = null;
	  private static File filePath;
	  private static File filePath2;
	  private String service;
	  private static Properties properties;
	  private int readBufferSize;
	  private int writeBufferSize;
	  private int connectionPoolSize;
	  private int writeHeaderSize;
	  private int readHeaderSize;
	  private String confPath = "";

	  private boolean headerInSize = false;

	  private boolean httpServer = true;

	  private boolean responseHttp = true;

	  private boolean useNetty = false;
	  private byte[] relocateUrl;
	  private String responseData = "HTTP/1.1 200 OK\r\nContent-type: text/html\r\nContent-Length: 28\r\n\r\n{\r\n\"ret\":0,\r\n\"msg\":\"OK\"\r\n}\r\n";

	  private String crossDomain = "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>";
	  private MessageFilter messageFilter;
	  private static final Configuration instance = new Configuration();

	  public String getPrefix()
	  {
	    return this.prefix;
	  }

	  public boolean isResponseHttp()
	  {
	    return this.responseHttp;
	  }

	  public void setResponseHttp(boolean responseHttp) {
	    this.responseHttp = responseHttp;
	  }

	  public boolean isHttpServer() {
	    return this.httpServer;
	  }

	  public byte[] getRelocateUrl()
	  {
	    return this.relocateUrl;
	  }

	  public String getResponseData()
	  {
	    return this.responseData;
	  }

	  public String getCrossDomain()
	  {
	    return this.crossDomain;
	  }

	  public void setCrossDomain(String crossDomain) {
	    this.crossDomain = crossDomain;
	  }

	  public boolean isHeaderInSize() {
	    return this.headerInSize;
	  }

	  public String getConfPath() {
	    return this.confPath;
	  }

	  public void setConfPath(String xmlPath) {
	    this.confPath = xmlPath;
	  }

	  public int getReadHeaderSize() {
	    return this.readHeaderSize;
	  }

	  public void setReadHeaderSize(int readHeaderSize) {
	    this.readHeaderSize = readHeaderSize;
	  }

	  public int getWriteHeaderSize() {
	    return this.writeHeaderSize;
	  }

	  public int getConnectionPoolSize() {
	    return this.connectionPoolSize;
	  }

	  public int getReadBufferSize() {
	    return this.readBufferSize;
	  }

	  public int getWriteBufferSize() {
	    return this.writeBufferSize;
	  }

	  public MessageFilter getMessageFilter()
	  {
	    return this.messageFilter;
	  }

	  public void setMessageFilter(MessageFilter messageFilter) {
	    this.messageFilter = messageFilter;
	  }

	  public void setup(File directory, File directory2, String prefix) throws IOException {
	    filePath = directory;
	    if (directory2 != null)
	      filePath2 = directory2;
	    else {
	      filePath2 = filePath;
	    }
	    this.prefix = prefix;
	    properties = CfgToolbox.loadAllProperties(directory, directory2);

	    this.service = properties.getProperty(prefix + "services");
	    this.readBufferSize = Integer.parseInt(properties.getProperty(prefix + "readBufferSize").trim());
	    this.writeBufferSize = Integer.parseInt(properties.getProperty(prefix + "writeBufferSize").trim());
	    this.connectionPoolSize = Integer.parseInt(properties.getProperty(prefix + "connectionPoolSize").trim());
	    if (properties.getProperty(prefix + "writeHeaderSize") != null)
	      this.writeHeaderSize = Integer.parseInt(properties.getProperty(prefix + "writeHeaderSize").trim());
	    else {
	      this.writeHeaderSize = 2;
	    }
	    if (properties.getProperty(prefix + "readHeaderSize") != null)
	      this.readHeaderSize = Integer.parseInt(properties.getProperty(prefix + "readHeaderSize").trim());
	    else {
	      this.readHeaderSize = 2;
	    }
	    this.confPath = properties.getProperty(prefix + "path");
	    if ((this.confPath == null) || (this.confPath.isEmpty())) {
	      this.confPath = ".";
	    }

	    String insize = properties.getProperty(prefix + "headerInsize");
	    if ((insize == null) || (insize.isEmpty()))
	      this.headerInSize = false;
	    else {
	      this.headerInSize = Boolean.parseBoolean(insize.trim());
	    }

	    String http = properties.getProperty(prefix + "isHttpServer");
	    if ((http == null) || (http.isEmpty()))
	      this.httpServer = false;
	    else {
	      this.httpServer = Boolean.parseBoolean(http.trim());
	    }

	    String useNettyStr = properties.getProperty(prefix + "useNetty");
	    if ((useNettyStr == null) || (!useNettyStr.equals("true")))
	      this.useNetty = false;
	    else {
	      this.useNetty = true;
	    }

	    String response = properties.getProperty(prefix + "responseHttp");
	    if ((response == null) || (response.isEmpty()))
	      this.responseHttp = true;
	    else {
	      this.responseHttp = Boolean.parseBoolean(response.trim());
	    }

	    String messageFilterConf = properties.getProperty(prefix + "messageFilter");
	    if (messageFilterConf != null) {
	      try {
	        this.messageFilter = ((MessageFilter)Class.forName(messageFilterConf.trim()).newInstance());
	      } catch (InstantiationException e) {
	        e.printStackTrace();
	      } catch (IllegalAccessException e) {
	        e.printStackTrace();
	      } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	      }
	    }

	    String xml = properties.getProperty("crossDomain");
	    if (xml != null)
	      this.crossDomain = xml.trim();
	    String urlFilePath;
	    if (directory2 != null)
	      urlFilePath = directory2.getPath();
	    else {
	      urlFilePath = directory.getPath();
	    }
	    this.relocateUrl = loadUrlRelocate(urlFilePath);

	    loadSlf4jConfig();
	  }

	  private byte[] loadUrlRelocate(String path) {
	    byte[] data = null;
	    try {
	      FileInputStream stream = new FileInputStream(path + "/relocate.txt");
	      data = ByteUtil.readAllData(stream);
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return data;
	  }

	  private void loadSlf4jConfig() {
	    String filePathStr = filePath2.getPath();
	    if (!filePathStr.endsWith("/")) {
	      filePathStr = filePathStr + "/";
	    }
	    String slf4jPath = filePathStr + this.prefix + "log4j.xml";
	    try {
	      DOMConfigurator.configure(slf4jPath);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  public static File getFilePath()
	  {
	    return filePath;
	  }

	  public static File getFilePath2() {
	    return filePath2;
	  }
	  public Connection getConnection(int sessionId){
		 return  HandlerDispatcher.getConnection(sessionId);
	  }
	  public int getClientPort()
	  {
	    return Integer.parseInt(properties.getProperty(this.prefix + "client_port").trim());
	  }

	  public InetAddress getClientBindAddress() {
	    return this.clientBindAddress;
	  }

	  public void setClientBindAddress(InetAddress clientBindAddress) {
	    this.clientBindAddress = clientBindAddress;
	  }

	  public String getServerIP() {
	    return this.serverIP;
	  }

	  public void setServerIP(String serverIP) {
	    this.serverIP = serverIP;
	  }

	  public String getServiceName() {
	    return this.service;
	  }

	  public Properties getLowercaseConfigurationFor(String prefix) {
	    return getConfigurationForSection(prefix, properties, false);
	  }

	  public Properties getUppercaseConfigurationFor(String prefix) {
	    return getConfigurationForSection(prefix, properties, false);
	  }

	  public Properties getConfigurationFor(String prefix) {
	    return getConfigurationForSection(prefix, properties);
	  }

	  public static Properties getConfigurationForSection(String prefix, Properties properties) {
	    prefix = prefix.toLowerCase() + "-";
	    Properties result = new Properties();
	    Enumeration en = properties.keys();
	    while (en.hasMoreElements()) {
	      String key = (String)en.nextElement();
	      if (key.startsWith(prefix)) {
	        result.setProperty(key.substring(prefix.length()), properties.getProperty(key));
	      }
	    }
	    return result;
	  }

	  public static final Properties getConfigurationForSection(String prefix, Properties properties, boolean upperCase) {
	    prefix = prefix.toLowerCase() + "_";
	    Properties result = new Properties();
	    Enumeration<Object> en = properties.keys();
	    while (en.hasMoreElements()) {
	      String key = (String)en.nextElement();
	      String value = properties.getProperty(key);
	      if (upperCase)
	        key = key.toUpperCase();
	      else {
	        key = key.toLowerCase();
	      }
	      if (key.startsWith(prefix)) {
	        result.setProperty(key.substring(prefix.length()), value);
	      }
	    }
	    return result;
	  }

	  public static Properties getProperties() {
	    return properties;
	  }

	  public static Configuration getInstance()
	  {
	    return instance;
	  }

	  public boolean isUseNetty() {
	    return this.useNetty;
	  }

	  public void setUseNetty(boolean useNetty) {
	    this.useNetty = useNetty;
	  }
	}