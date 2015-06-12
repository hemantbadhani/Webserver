package hbadhani.webserver.network;

import hbadhani.webserver.interfaces.ReqListner;
import hbadhani.webserver.interfaces.ReqProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;



public class NetworkReqListner implements ReqListner{
	
	private ServerSocket serverSock;	
	private ReqProcessor reqProcessor;
	static Logger logger = Logger.getLogger("webserver.network");
	
	public NetworkReqListner(int port,ReqProcessor reqProcessor) throws IOException
	{		
		this.reqProcessor = reqProcessor;
		
		//Open a socket at the configured port number to listen to requests
		serverSock = new ServerSocket(port);
		logger.fine("New server socket at port:" + port);
	}

	public void listen() throws IOException
	{
		logger.fine("Ready to listen");
		
		while(true)
		{
			NetworkConnection connection = new NetworkConnection(serverSock.accept());
			logger.finer("New request received");
			//Request will processed by an appropriate request processor in a separate thread
			reqProcessor.processRequest(connection);					
		}		
	}

}
