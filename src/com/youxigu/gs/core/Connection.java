package com.youxigu.gs.core;

import com.youxigu.gs.message.Message;
import com.youxigu.gs.util.ByteUtil;
import com.youxigu.gs.util.NettyUtil;

import io.netty.channel.Channel;

public class Connection {
	protected Channel channel;
	private Service service;
	private int processorId;
	public boolean isConnected(){
	    return this.channel.isActive();
	}
	public Object getObject() {
		return this.channel.attr(NettyUtil.player).get();
	}

	public void setObject(Object object) {
		this.channel.attr(NettyUtil.player).set(object);
	}

	public int getSessionID() {
		return ((Integer) this.channel.attr(NettyUtil.sessionId).get()).intValue();
	}

	public void pushMessage(Message message) {
		message.setPushed(true);
		HandlerExecutor.ins().execute(message.getConnection().getProcessorId(),
				new Runnable() {
					public void run() {
						Connection.this.service.processDown(message);
					}
				});
	}

	public void sendMessage(short type, byte[] data) {
		byte[] msgData = new byte[data.length
				+ Configuration.getInstance().getWriteHeaderSize() + 2];
		MessageFilter filter = Configuration.getInstance().getMessageFilter();
		if (Configuration.getInstance().getWriteHeaderSize() == 2) {
			ByteUtil.setShort(msgData, 0, data.length + 2);
			ByteUtil.setShort(msgData, 2, type);
			System.arraycopy(data, 0, msgData, 4, data.length);
			if (filter != null)
				msgData = filter.encrypt(msgData, 2);
		} else {
			ByteUtil.setInt(msgData, 0, data.length + 2);
			ByteUtil.setShort(msgData, Configuration.getInstance()
					.getWriteHeaderSize(), type);
			System.arraycopy(data, 0, msgData, Configuration.getInstance()
					.getWriteHeaderSize() + 2, data.length);
			if (filter != null) {
				msgData = filter.encrypt(msgData, 4);
			}
		}
		Message bm = new Message(this, msgData);
		this.channel.writeAndFlush(bm);
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void killConnection() {
		this.channel.close();
	}

	public void setService(Service service) {
		this.service = service;
	}

	public int getProcessorId() {
		return this.processorId;
	}

	public void setProcessorId(int processorId) {
		this.processorId = processorId;
	}
}
