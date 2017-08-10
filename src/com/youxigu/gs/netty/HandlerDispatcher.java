package com.youxigu.gs.netty;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youxigu.gs.core.Connection;
import com.youxigu.gs.core.HandlerExecutor;
import com.youxigu.gs.core.Service;
import com.youxigu.gs.message.Message;
import com.youxigu.gs.util.NettyUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HandlerDispatcher extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(HandlerDispatcher.class);
	private Service service;
	private AtomicInteger sessionId;
	private AtomicInteger processId;
	private static Map<Integer, Connection> clients = new ConcurrentHashMap<>();
	public static Connection getConnection(int sessionId){
		return clients.get(sessionId);
	}
	public HandlerDispatcher(Service service, AtomicInteger sessionId,AtomicInteger processId) {
		this.service = service;
		this.sessionId = sessionId;
		this.processId = processId;
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		byte[] array = (byte[]) (byte[]) msg;
		logger.debug(Arrays.toString(array));
		byte[] result = new byte[array.length - 2];
		System.arraycopy(msg, 2, result, 0, array.length - 2);
		Object player = ctx.channel().attr(NettyUtil.player).get();
		int sessionId = ((Integer) ctx.channel().attr(NettyUtil.sessionId)
				.get()).intValue();
		Connection connection = clients.get(sessionId);
		connection.setObject(player);
		Message message = new Message(connection, result);
		HandlerExecutor.ins().execute(connection.getProcessorId(),
				new Runnable() {
					public void run() {
						HandlerDispatcher.this.service.processDown(message);
					}
				});
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Object p = ctx.channel().attr(NettyUtil.player).get();
		int sessionId = ((Integer) ctx.channel().attr(NettyUtil.sessionId)
				.get()).intValue();
		Connection connection = clients.get(sessionId);
		if (null != p)
			HandlerExecutor.ins().execute(connection.getProcessorId(),
					new Runnable() {
						public void run() {
							Message message = new Message(connection, Message.USER_DISCONNECTED);
							HandlerDispatcher.this.service.processDown(message);
						}
					});
		clients.remove(sessionId);
		ctx.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
	}

	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(NettyUtil.sessionId)
				.set(Integer.valueOf(this.sessionId.incrementAndGet()));
		Connection connection = new Connection();
		connection.setService(this.service);
		connection.setProcessorId(this.processId.incrementAndGet() % HandlerExecutor.num);
		connection.setChannel(ctx.channel());
		logger.info("user connected ,processId:"+connection.getProcessorId());
		clients.put(connection.getSessionID(), connection);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.debug(cause.getMessage());
		Object p = ctx.channel().attr(NettyUtil.player).get();
		int sessionId = ((Integer) ctx.channel().attr(NettyUtil.sessionId)
				.get()).intValue();
		Connection connection = clients.get(sessionId);
		if (null != p)
			HandlerExecutor.ins().execute(connection.getProcessorId(),
					new Runnable() {
						public void run() {
							Message message = new Message(connection, Message.USER_DISCONNECTED);
							HandlerDispatcher.this.service.processDown(message);
						}
					});
		clients.remove(sessionId);
		ctx.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
	}
}
