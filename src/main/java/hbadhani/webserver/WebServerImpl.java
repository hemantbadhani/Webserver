package hbadhani.webserver;


import hbadhani.webserver.http.HttpRequestProcessor;
import hbadhani.webserver.network.NetworkReqListner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This is the main webserver class which starts the server.
 */
public class WebServerImpl {
	/**
	 *
	 * @param fileName Name of .properties file containing configuration details.<br>
	 *                 <b>File Content</b>
	 *                 PortNo   -   Name of the port on which the server should listen.Default:8080<br>
	 *                 Threads  -   Maximum number of threads processing simultaneous HTTP requests.Default:1<br>
	 *                 LogFilePath  -   File in which logs need to be dumped.Default:Logs are not dumped in file.<br>
	 *                 LogLevel -   SEVERE/WARNING/INFO/CONFIG/FINE/FINER/FINEST/ALL/OFF<br>
	 *                 <a href="https://docs.oracle.com/javase/7/docs/api/java/util/logging/Level.html">Level</a>
	 *
	 *
	 * @return
	 */

	public Config readConfig(String fileName){
		if(null != fileName) {
			File configFile = new File(fileName);
			Config config = new Config();
			try {
				FileReader reader = new FileReader(configFile);
				Properties props = new Properties();
				props.load(reader);
				String val = props.getProperty("PortNo");
				config.port = Integer.parseInt(val);
				val = props.getProperty("Threads");
				config.nThreads = Integer.parseInt(val);
				val = props.getProperty("LogFilePath");
				config.logFilePath = val;
				reader.close();
			} catch (FileNotFoundException ex) {
				// file does not exist
			} catch (IOException ex) {
				// I/O error
			}

			return config;
		}
		return null;
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		Logger logger = null;
		Handler logFileHandler = null;

		try
		{
			Config config = new WebServerImpl().readConfig("config.properties");
			//Create a logger
			logger = Logger.getLogger(String.valueOf(WebServerImpl.class));
			if (!Files.isDirectory(Paths.get(".logs"))){
				new File(".logs").mkdir();
			}
			File logFile = new File(config.logFilePath);
			if(!Files.isDirectory(Paths.get(logFile.getParent())))
				new File(logFile.getParent()).mkdirs();
			//Add a handler which dumps the logs in a file
			logFileHandler = new FileHandler(config.logFilePath, false);
			logger.addHandler(logFileHandler);
			//Set log level
			logger.setLevel(Level.ALL);
			
			NetworkReqListner reqListner = new NetworkReqListner(config.port,new HttpRequestProcessor(config.nThreads));
			reqListner.listen();			
				
		}catch(Exception e){
			e.printStackTrace();			
		}
		finally
		{
			logFileHandler.close();			
		}
		
		
	}
	private class Config{
		private int port = 8080;
		private int nThreads = 1;
		private String logFilePath = "logs.xml";
	}
}
