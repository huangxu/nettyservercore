package com.youxigu.gs.pool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectPool<T extends IObject> {
	private HashMap<IObject, IObject> objects = null;

	private Queue<T> unUsedObjects = null;

	private final ReentrantLock lockObj = new ReentrantLock();

	public IObject borrow() throws Exception {
		this.lockObj.lock();
		try {
			IObject obj = (IObject) this.unUsedObjects.poll();
			if (obj != null) {
				this.objects.put(obj, obj);
				IObject localIObject1 = obj;
				return localIObject1;
			}
			throw new Exception("Not enought Object in Pool , unUsedSize="
					+ this.unUsedObjects.size() + ", usedSize="
					+ this.objects.size());
		} finally {
			this.lockObj.unlock();
		}
	}

	public void back(T obj) throws Exception {
		obj.reset();
		this.lockObj.lock();
		try {
			IObject removed = (IObject) this.objects.remove(obj);
			if (removed != null)
				this.unUsedObjects.add(obj);
			else {
				throw new Exception("No object in use pool , unUsedSize="
						+ this.unUsedObjects.size() + ", usedSize="
						+ this.objects.size());
			}
		} finally {
			this.lockObj.unlock();
		}
	}

	public boolean addObject(T obj) {
		this.lockObj.lock();
		boolean bool = false;
		try {
			if (null != obj) {
				bool = this.unUsedObjects.add(obj);
				return bool;
			}
			return bool;
		} finally {
			this.lockObj.unlock();
		}
	}

	public void initPoolSize(int size) {
		if (this.objects == null) {
			this.objects = new HashMap<>(size);
			this.unUsedObjects = new LinkedList<>();
		}
	}

	public int getUnusedSize() {
		return this.unUsedObjects.size();
	}

	public int getUsedSize() {
		return this.objects.size();
	}
}
