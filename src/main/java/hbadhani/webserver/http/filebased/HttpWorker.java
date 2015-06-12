package hbadhani.webserver.http.filebased;

/**
 * Created by hbadhani on 12/06/2015.
 */

import hbadhani.webserver.http.HttpRequest;
import hbadhani.webserver.http.HttpResponse;
import hbadhani.webserver.interfaces.Connection;

import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class HttpWorker implements Runnable{

	Connection connection;
	BufferedReader reader = null;
	private final static String root = "/";
	private final static String sitesPath = "sites";
	private final static String rootPage = "index.html";
	private final static Logger logger = Logger.getLogger("hbadhani.webserver.http.filebased.HttpWorker");

	public HttpWorker(Connection connection)
	{
		this.connection = connection;
	}

	public void run() {

		try {
			//TODO: Remove hard coding
			connection.setTimeOut(5000);/*Set default timeout to 5000 miliseconds*/
			HttpRequest httpReq = parseHttpRequest();
			if(httpReq != null)
			{
				logger.finer(httpReq.type.strVal + ":" + httpReq.url);
				if(httpReq.type == HttpRequest.RequestType.GET || httpReq.type == HttpRequest.RequestType.HEAD){
					processGetOrHeadRequest(httpReq);
				}else if(httpReq.type == HttpRequest.RequestType.POST){
					processPostRequest(httpReq);
				}else{
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

	private HttpRequest parseHttpRequest()
	{
		HttpRequest httpReq = new HttpRequest();
		if(reader == null)
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		try {

			String[] req 		= reader.readLine().split(" ");

			if(HttpRequest.RequestType.GET.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = HttpRequest.RequestType.GET;
			}
			else  if(HttpRequest.RequestType.HEAD.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = HttpRequest.RequestType.HEAD;
			}
			else if(HttpRequest.RequestType.POST.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = HttpRequest.RequestType.POST;
			}
			else  if(HttpRequest.RequestType.OPTIONS.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = HttpRequest.RequestType.OPTIONS;
			}
			else  if(HttpRequest.RequestType.PUT.strVal.equalsIgnoreCase(req[0]))
			{
				httpReq.type = HttpRequest.RequestType.PUT;
			}
			else
			{
				logger.fine("Unknown HTTP request");
				httpReq.type = HttpRequest.RequestType.UNKNOWN;
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

			StringBuffer url = new StringBuffer(sitesPath.concat(httpReq.url));
			logger.finest("Request for:" + url);
			if(new File(url.toString()).isDirectory()){
				url.append(root);
				url.append(rootPage);
			}
			else if(url.toString().endsWith(root)){
				url.append(rootPage);
			}
			File responsePage = new File(url.toString());
			if(!responsePage.isFile()) {
				send404NotFound(httpReq);
				return;
			}
			long contentLength = responsePage.length();
			String contentType = null;
			if(url.toString().endsWith(".HTML") || url.toString().endsWith(".TXT")){
				contentType = HttpResponse.ContentTypeVal.TEXT_HTML.strVal;
			}
			else if(url.toString().toUpperCase().endsWith(".PNG")){
				contentType = HttpResponse.ContentTypeVal.IMAGE_PNG.strVal;
			}
			else if(url.toString().toUpperCase().endsWith(".JPG")){
				contentType = HttpResponse.ContentTypeVal.IMAGE_JPEG.strVal;
			}
			else if(url.toString().toUpperCase().endsWith(".GIF")){
				contentType = HttpResponse.ContentTypeVal.IMAGE_GIF.strVal;
			}
			else if(url.toString().toUpperCase().endsWith(".CSS")){
				contentType = HttpResponse.ContentTypeVal.TEXT_CSS.strVal;
			}
			connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
					HttpResponse.ResponseCode.OK_200.strVal + "\n" +
					HttpResponse.Field.CONTENT_TYPE.strVal + " " +
					contentType + "\n" +
					HttpResponse.Field.CONTENT_LENGTH.strVal + " "+
					contentLength + "\n" +
					"\n").getBytes() );
			if(httpReq.type == HttpRequest.RequestType.GET) {
				copyStreamData(new FileInputStream(url.toString()), connection.getOutputStream());
			}
		}catch(Exception e){
			logger.finer("Failed to process GET request for" + httpReq.url);
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
					logger.finest("POST data size:" + contentLength);
					break;
				}
			}
			//Ignore rest of the header
			while((null != (line = reader.readLine()))
					&&  line.length() != 0);
			int formDataRead = 0;
			int fc;
			int i = 0;
//			/*Check if it's a 'comment'*/
//			for(i=0;i<"comment=".length() && (formDataRead < contentLength);i++)
//			{
//				fc = reader.read();
//				formDataRead++;
//				if(fc != "comment=".charAt(i))
//				{
//					/*TODO:Invalid POST*/
//					return;
//				}
//			}
//			/*if not a 'comment', return*/
//			if(i != "comment=".length())
//			{
//				/*TODO:Invalid POST*/
//				return;
//			}
//			/*Read 'comment'*/
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
			logger.finest("Post data:" + commentStr);
			String resp = "Thank you for contacting us.";
			logger.finest("Sending 200OK");
			connection.getOutputStream().write((HttpResponse.Field.HTTP_1_1.strVal + " "	+
					HttpResponse.ResponseCode.OK_200.strVal + "\n" +
					HttpResponse.Field.CONTENT_TYPE.strVal + " " +
					HttpResponse.ContentTypeVal.TEXT_HTML.strVal + "\n" +
					HttpResponse.Field.CONTENT_LENGTH.strVal + " "+
					resp.getBytes().length + "\n" +
					"\n").getBytes() );
			connection.getOutputStream().write(resp.getBytes());
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
			logger.fine("Failed to send 501");
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


