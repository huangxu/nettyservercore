package com.youxigu.gs.core;

import java.nio.ByteBuffer;

import com.youxigu.gs.message.Message;

public interface Handler {
	public abstract void execute(Object paramObject, Message message, ByteBuffer buffer);
}
