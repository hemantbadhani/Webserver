package hbadhani.webserver.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines an interface for reading and writing data. * 
 */
public interface Connection {
	
	InputStream getInputStream();
	OutputStream getOutputStream();
	void setTimeOut(int timeout);//timeout is in milliseconds
	void close() throws IOException;
}

