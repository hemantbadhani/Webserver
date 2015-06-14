package hbadhani.webserver;


import hbadhani.webserver.http.HttpRequestProcessor;
import hbadhani.webserver.network.NetworkReqListner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.*;


/**
 * This is the main webserver class which starts the server.
 */
public class WebServerImpl {

	static Logger logger = null;
	static Handler logFileHandler = null;

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

	protected Config readConfig(String fileName){
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
				val = props.getProperty("LogLevel");
				config.logLevel = val;
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


		try
		{
			//Register a shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					if(null != logger){
						logger.severe("Shutting down server!");
					}
					if(null != logFileHandler){
						logFileHandler.close();
					}
				}
			}, "Shutdown-Thread"));

			Config config = new WebServerImpl().readConfig("config.properties");

			//Create a logger
			logger = Logger.getLogger("hbadhani.webserver");
			File logFile = new File(config.logFilePath);
			if(!Files.isDirectory(Paths.get(logFile.getParent())))
				new File(logFile.getParent()).mkdirs();
			//Add a handler which dumps the logs in a file
			logFileHandler = new FileHandler(config.logFilePath, false);
			Formatter formatter = new SimpleFormatter();
			logFileHandler.setFormatter(formatter);
			logger.addHandler(logFileHandler);

			//Set log level
			if("ALL".equalsIgnoreCase(config.logLevel)) {
				logger.setLevel(Level.ALL);
			}else if ("SEVERE".equalsIgnoreCase(config.logLevel)){
				logger.setLevel(Level.SEVERE);
			}else if ("WARNING".equalsIgnoreCase(config.logLevel)){
				logger.setLevel(Level.WARNING);
			}else if ("INFO".equalsIgnoreCase(config.logLevel)) {
				logger.setLevel(Level.INFO);
			}else if ("CONFIG".equalsIgnoreCase(config.logLevel)) {
				logger.setLevel(Level.CONFIG);
			}else if ("FINE".equalsIgnoreCase(config.logLevel)) {
				logger.setLevel(Level.FINE);
			}else if ("FINER".equalsIgnoreCase(config.logLevel)) {
				logger.setLevel(Level.FINER);
			}else if ("FINEST".equalsIgnoreCase(config.logLevel)) {
				logger.setLevel(Level.FINEST);
			}else{
				logger.setLevel(Level.OFF);
			}
			logger.config("Log file path:" + logFile.getAbsolutePath());
			logger.config("Log level:" + logger.getLevel().toString());

			NetworkReqListner reqListner = new NetworkReqListner(config.port,new HttpRequestProcessor(config.nThreads));
			reqListner.listen();			
				
		}catch(Exception e){
			e.printStackTrace();			
		}
	}
	private static class Config{
		private int port = 8080;
		private int nThreads = 1;
		private String logFilePath = "logs.xml";
		private String logLevel = "OFF";
	}
}
