package com.thend.home.sweethome.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsyncExecutor {
	
	private static final Log logger = LogFactory.getLog(AsyncExecutor.class);

	private static ThreadPoolExecutor exec = new ThreadPoolExecutor(5, 10, 300,
	            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000),
	            new AsyncThreadFactory(), new DiscardOldestPolicy());
	 
	public static void submit(Runnable task) {
		try {
			exec.execute(task);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
