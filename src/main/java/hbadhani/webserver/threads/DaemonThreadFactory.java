package hbadhani.webserver.threads;

import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class DaemonThreadFactory implements ThreadFactory {

	static Logger logger = Logger.getLogger("hbadhani.webserver.threads.DaemonThreadFactory");

	public Thread newThread(Runnable r) {
		Thread th = new Thread(r);
		th.setDaemon(true);
		logger.finer("New daemon thread created");
		return th;
	}

}