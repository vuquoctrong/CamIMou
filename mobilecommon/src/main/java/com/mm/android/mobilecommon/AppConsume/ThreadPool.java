package com.mm.android.mobilecommon.AppConsume;

import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ThreadPool {
	private volatile static ExecutorService cachedThreadPool;

	/**
	 * Submit a thread
	 *
	 * 提交线程
	 */
	public static Future<?> submit(Runnable mRunnable) {

		if (cachedThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (cachedThreadPool == null) {
					cachedThreadPool = Executors.newFixedThreadPool(Runtime
							.getRuntime().availableProcessors() * 2,new DefaultFactory());
				}
			}
		}
		return cachedThreadPool.submit(mRunnable);
	}

	/**
	 * Shutdown
	 *
	 * 关闭
	 */
	public static void shutdown() {
		if (cachedThreadPool != null && !cachedThreadPool.isShutdown())
			cachedThreadPool.shutdown();
		cachedThreadPool = null;
	}

	static class DefaultFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {

			Thread thread = new Thread(new FactoryRunnable(r));

			return thread;
		}
	}

	static class FactoryRunnable implements Runnable {
		Runnable runnable;

		public FactoryRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			runnable.run();
		}

	}

}
