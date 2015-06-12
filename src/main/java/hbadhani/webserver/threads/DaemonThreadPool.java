package hbadhani.webserver.threads;


public class DaemonThreadPool extends ThreadPool {
	
	public DaemonThreadPool(int nThreads) {
		
		super(nThreads,new DaemonThreadFactory());				
	}

}
