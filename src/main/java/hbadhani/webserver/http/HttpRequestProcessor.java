package hbadhani.webserver.http;

import java.util.logging.Logger;

import hbadhani.webserver.http.filebased.HttpWorker;
import hbadhani.webserver.threads.DaemonThreadPool;
import hbadhani.webserver.interfaces.Connection;
import hbadhani.webserver.interfaces.ReqProcessor;


/**
 * This class implements the ReqProcessor interface for HTTP requests.
 *
 */
public class HttpRequestProcessor implements ReqProcessor{
	
	private DaemonThreadPool threadPool;
	private final static Logger logger = Logger.getLogger("hbadhani.webserver.http.HttpRequestProcessor");

	/**
	 * Constructor
	 * @param maxParallelReqs Maximum number of HTTP request processing threads
	 */
	public HttpRequestProcessor(int maxParallelReqs) {
		logger.fine("Creating pool of "+ maxParallelReqs +" daemon threads");
		//Create a thread pool for processing requests in parallel
		threadPool = new DaemonThreadPool(maxParallelReqs);
	}

	/**
	 * This method submits the connection to a thread pool, which in turn spawns HttpWorker to
	 * process the request
	 * @param connection HTTP request connection
	 */
	public void processRequest(Connection connection)
	{
		//logger.fine("Creating pool of "+ maxParallelReqs +" daemon threads");
		//Request will processed by an appropriate request processor in a separate thread
		threadPool.execute(new HttpWorker(connection));
	}
	

}
