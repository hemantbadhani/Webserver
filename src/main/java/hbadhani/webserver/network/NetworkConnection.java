package hbadhani.webserver.network;


import hbadhani.webserver.interfaces.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class NetworkConnection implements Connection{
	Socket sock;
	InputStream inputStream;
	OutputStream outputStream;
	
	NetworkConnection(Socket sock) throws IOException
	{
		this.sock = sock;
		inputStream = sock.getInputStream();
		outputStream = sock.getOutputStream();
	}	
	

	public InputStream getInputStream() {		
		return inputStream;
	}


	public OutputStream getOutputStream() {
		return outputStream;
	}
	

	public void close() throws IOException
	{		
		sock.close();
	}


	public void setTimeOut(int timeout){		
		try {
			sock.setSoTimeout(timeout);
		} catch (SocketException e) {			
			e.printStackTrace();
		}
	}
}
