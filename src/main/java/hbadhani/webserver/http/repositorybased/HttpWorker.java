package hbadhani.webserver.http.repositorybased;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.util.logging.Logger;


import hbadhani.webserver.http.HttpRequest;
import hbadhani.webserver.http.HttpRequest.RequestType;
import hbadhani.webserver.http.HttpResponse;
import hbadhani.webserver.interfaces.Connection;

public class HttpWorker implements Runnable{

	Connection connection;
	BufferedReader reader = null;

	static Logger logger = Logger.getLogger("webserver.http.HttpWorker"); 

	HttpWorker(Connection connection)
	{		
		this.connection = connection;		
	}

	public void run() {

		try {
			//TODO: Remove hard coding
			connection.setTimeOut(5000);/*Set default timeout to 5000 miliseconds*/
			HttpRequest httpReq = getHttpRequest();
			if(httpReq != null)
			{
				logger.finer(httpReq.type.strVal + ":" + httpReq.url);				
				if(httpReq.type == RequestType.GET || httpReq.type == RequestType.HEAD){
					processGetOrHeadRequest(httpReq);				
				}
				else if(httpReq.type == RequestType.POST){
					processPostRequest(httpReq);
				}
				else{
					send501Unimplemented(httpReq);
				}


			}
			connection.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block			
		}
	}	

	private void copyStreamData(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
	}

	private HttpRequest getHttpRequest()
	{
		HttpRequest httpReq = new HttpRequest();
		if(reader == null)
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		try {	

			String[] req 		= reader.readLine().split(" ");

			if(RequestType.GET.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = RequestType.GET;
			}
			else  if(RequestType.HEAD.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = RequestType.HEAD;
			}
			else if(RequestType.POST.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = RequestType.POST;	        	
			}
			else  if(RequestType.OPTIONS.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = RequestType.OPTIONS;
			}
			else  if(RequestType.PUT.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = RequestType.PUT;
			}
			else
			{
				logger.fine("Unknown HTTP request");
				httpReq.type = RequestType.UNKNOWN;	        	
			}

			httpReq.url = req[1];

			/* 	while ((line = reader.readLine()) != null) {
				if(line.isEmpty())
					break;
				completeReqHdr.append("\n" + line);

			}*/	        
			return httpReq;
		} catch (IOException e) {
			return null;			
		}

	}	
	void processGetOrHeadRequest(HttpRequest httpReq){	
		try{			
			HttpResourceRepository.loadResourceMapFromXML();
			HttpResource resource = HttpResourceRepository.getResource(httpReq.url);
			if(resource != null){
				long contentLength = 0;

				if(resource.type.equalsIgnoreCase(HttpResource.ResourceType.HTML_TEXT.strVal))
				{
					File resFile = new File(resource.value);
					contentLength = resFile.length();						
					connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
							HttpResponse.ResponseCode.OK_200.strVal + "\n" +
							HttpResponse.Field.CONTENT_TYPE.strVal + " " +							
							HttpResponse.ContentTypeVal.TEXT_HTML.strVal + "\n" +
							HttpResponse.Field.CONTENT_LENGTH.strVal + " "+ 
							contentLength + "\n" +
							"\n").getBytes() );
					if(httpReq.type == RequestType.GET)
						copyStreamData(new FileInputStream(resource.value), connection.getOutputStream());
				}
				if(resource.type.equalsIgnoreCase(HttpResource.ResourceType.IMAGE_PNG.strVal))
				{
					File resFile = new File(resource.value);
					contentLength = resFile.length();						
					connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
							HttpResponse.ResponseCode.OK_200.strVal + "\n" +
							HttpResponse.Field.CONTENT_TYPE.strVal + " " +							
							HttpResponse.ContentTypeVal.IMAGE_PNG.strVal + "\n" +
							HttpResponse.Field.CONTENT_LENGTH.strVal + " "+ 
							contentLength + "\n" +
							"\n").getBytes() );
					if(httpReq.type == RequestType.GET)
						copyStreamData(new FileInputStream(resource.value), connection.getOutputStream());
				}
			}
			else{
				logger.finer(httpReq.url + " Not Found");
				send404NotFound(httpReq);
			}
		}catch(Exception e){
			logger.finer("Failed to process GET request for httpReq.resourceName");
		}

	}
	void processPostRequest(HttpRequest httpReq){
		try {
			String[] header = null;
			String line = null;	    
			long contentLength = 0;
			long fileSize = 0;
			long dataRead = 0;
			
				
			
			while( null != (header = reader.readLine().split(" ")))
			{	        		
				if(HttpResponse.Field.CONTENT_LENGTH.strVal.trim().equalsIgnoreCase(header[0]))
				{
					contentLength = Long.parseLong(header[1], 10);
					System.out.println("POST data size:" + contentLength);
					break;
				}		

			}
			//Ignore rest of the header
			while((null != (line = reader.readLine()))
					&&  line.length() != 0);
			int formDataRead = 0;
			int fc;
			int i = 0;
			/*Check if it's a 'comment'*/
			for(i=0;i<"comment=".length() && (formDataRead < contentLength);i++)
			{
				fc = reader.read();
				formDataRead++;
				if(fc != "comment=".charAt(i))
				{					
					/*TODO:Invalid POST*/
					return;
				}					
			}
			/*if not a 'comment', return*/
			if(i != "comment=".length())
			{
				/*TODO:Invalid POST*/
				return;
			}
			/*Read 'comment'*/
			byte[] comment = null;
			if(contentLength > formDataRead)
			{
				comment = new byte[(int)(contentLength - formDataRead)];			
			}
			for(i=0;i<(contentLength-formDataRead);i++)
			{
				comment[i] = (byte)reader.read();
			}
			/*Save entry in comments db file*/
			String commentStr = new String(comment);			
			HttpResource commentsDB = HttpResourceRepository.getResource("/comments","DB");
			if(commentsDB != null)
			{
				
				File db = new File(commentsDB.value);
				FileWriter writer = new FileWriter(db,true);
				writer.write("<comment>");
				writer.write(commentStr);
				writer.write("</comment>");	
				writer.close();
			}
			
			connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
					HttpResponse.ResponseCode.OK_200.strVal + "\n" +
					HttpResponse.Field.CONTENT_TYPE.strVal + " " +							
					HttpResponse.ContentTypeVal.IMAGE_PNG.strVal + "\n" +
					HttpResponse.Field.CONTENT_LENGTH.strVal + " "+ 
					0 + "\n" +
					"\n").getBytes() );	
			
			//check if it's a comment
			
			while((null != (line = reader.readLine()))
					&&  line.length() != 0)
			{
				dataRead += line.length();
				if(line.startsWith("Content-Disposition"))
				{
					int nameIndx = line.indexOf("name=");        			
					if(nameIndx > 0)
					{
						if(line.substring(nameIndx + "name=".length()).startsWith("\"upload\""))
						{        					
							int fileNmIndx = line.indexOf("filename=");
							if(fileNmIndx > 0)
							{                		
								String fileName = line.substring(fileNmIndx + "filename=".length()).replace("\"","");
								System.out.println("Upload request recevied for " + fileName + "(" + fileSize + " Bytes)");
								//Ignore rest of the header
								while((null != (line = reader.readLine()))
										&&  line.length() != 0){
									dataRead += line.length();
								}

								File file = new File(fileName);
								BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));               				

								System.out.println("contentLength = " + contentLength);
								long fileCharRead = 0;
								while(fileSize > fileCharRead)
								{
									int ch = reader.read();
									fileWriter.write(ch);                					
									fileCharRead++;
								}
								System.out.println("closing:" + (contentLength - (dataRead + fileCharRead)));
								fileWriter.close();                				
							}

						}
					}
				}
			}
			System.out.println("Sending 200OK");
			connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
					HttpResponse.ResponseCode.OK_200.strVal + "\n" +
					HttpResponse.Field.CONTENT_TYPE.strVal + " " +							
					HttpResponse.ContentTypeVal.TEXT_HTML.strVal + "\n" +
					HttpResponse.Field.CONTENT_LENGTH.strVal + " "+ 
					0 + "\n" +
					"\n").getBytes() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void send501Unimplemented(HttpRequest httpReq){
		try {
			connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
					HttpResponse.ResponseCode.UNIMPLEMENTED_501.strVal + "\n" +
					HttpResponse.Field.CONTENT_TYPE.strVal + " " +							
					HttpResponse.ContentTypeVal.TEXT_HTML.strVal + "\n" +
					HttpResponse.Field.CONTENT_LENGTH.strVal + " "+ 
					0 + "\n" +
					"\n").getBytes() );
		} catch (IOException e) {
			logger.fine("Failed to send 403");
		}

	}
	private void send404NotFound(HttpRequest httpReq){
		try {
			connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
					HttpResponse.ResponseCode.NOT_FOUND_404.strVal + "\n" +
					HttpResponse.Field.CONTENT_TYPE.strVal + " " +							
					HttpResponse.ContentTypeVal.TEXT_HTML.strVal + "\n" +
					HttpResponse.Field.CONTENT_LENGTH.strVal + " "+ 
					0 + "\n" +
					"\n").getBytes() );
		} catch (IOException e) {
			logger.fine("Failed to send 403");
		}
	}
}	

