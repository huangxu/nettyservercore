package com.youxigu.gs.netty;

import java.util.concurrent.atomic.AtomicInteger;

import com.youxigu.gs.core.Service;
import com.youxigu.gs.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
	private Service service  ;
	private  AtomicInteger sessionId = new AtomicInteger();
	private  AtomicInteger processId = new AtomicInteger();
	public NettyServerInitializer(Service service,AtomicInteger sessionId,AtomicInteger processId) {
		this.service = service;
		this.sessionId = sessionId;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ChannelHandler[] { new LengthFieldBasedFrameDecoder(1048576, 0, 2) });
		pipeline.addLast(new ChannelHandler[] { new ByteArrayDecoder() });
		pipeline.addLast(new ChannelHandler[] { new HandlerDispatcher(this.service,this.sessionId,this.processId) });
		pipeline.addLast(new ChannelHandler[] { new MessageToByteEncoder<Message>(){
			@Override
			protected void encode(ChannelHandlerContext chx, Message msg,
					ByteBuf out) throws Exception {
				  byte[] data = msg.getData();
		          out.writeBytes(data);
			}
			
		}});
	}

}
