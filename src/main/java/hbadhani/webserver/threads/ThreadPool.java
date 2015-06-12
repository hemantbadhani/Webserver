package hbadhani.webserver.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ThreadPool implements Executor{
	private ExecutorService executor;
	private final static Logger logger = Logger.getLogger("hbadhani.webserver.threads.threadpool");
	
	public ThreadPool(int nThreads,ThreadFactory threadFactory) {
		logger.config("nThreads:" + nThreads);
		if(nThreads < 1)
		{
			throw new IllegalArgumentException("Number of ThreadPool threads can't be less than 1");
		}
		this.executor = Executors.newFixedThreadPool(nThreads,threadFactory);		
	}

	public void execute(Runnable worker){
		if(null != executor)
		{			
			executor.execute(worker);
			logger.finest("New worker added");
			return;
		}
		else
		{
			logger.severe("ThreadPool hasn't been initialized");
			throw new NullPointerException();
		}
	}
}
