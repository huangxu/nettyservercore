package com.youxigu.gs.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youxigu.gs.core.Configuration;
import com.youxigu.gs.core.Service;

public class NettyServer {

	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private Service service;
	private static AtomicInteger sessionId = new AtomicInteger();
	private static AtomicInteger processId = new AtomicInteger();
	private Channel serverChannel;
	public NettyServer(Service service) {
		this.service = service;
	}
	public void closeServer() {
		logger.info("close netty server");
		this.serverChannel.close();
		logger.info("close channel finish");
	}
	public void start() throws InterruptedException {
		logger.info("start netty server");
		logger.info("Server's readBufferSize is "
				+ Configuration.getInstance().getReadBufferSize());
		logger.info("Server's writeBufferSize is "
				+ Configuration.getInstance().getWriteBufferSize());
		NioEventLoopGroup boos = new NioEventLoopGroup(1,
				new DefaultThreadFactory("server boss"));
		NioEventLoopGroup worker = new NioEventLoopGroup(4,
				new DefaultThreadFactory("server worker"));
		ServerBootstrap server = new ServerBootstrap();
		server.childOption(ChannelOption.SO_SNDBUF, Integer
				.valueOf(Configuration.getInstance().getWriteBufferSize()));
		server.childOption(ChannelOption.SO_RCVBUF, Integer
				.valueOf(Configuration.getInstance().getReadBufferSize()));
		server.childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
		server.childOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
		server.group(boos, worker)
				.channel(NioServerSocketChannel.class)
				.childHandler(
						new NettyServerInitializer(this.service, sessionId,
								processId));
		int clientPort = Configuration.getInstance().getClientPort();
		ChannelFuture f = server.bind(clientPort).sync();
		logger.info("start server on :{}", Integer.valueOf(clientPort));
		this.serverChannel = f.channel();
		this.serverChannel.closeFuture().sync();
		worker.shutdownGracefully();
		boos.shutdownGracefully();
		logger.info("netty server close done!");
	}
}
