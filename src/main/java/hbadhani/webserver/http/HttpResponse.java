package hbadhani.webserver.http;

/**
 *
 */
public class HttpResponse {

	/**
	 *
	 */
	public enum ResponseCode{
		CONTINUE_100("100 CONTINUE"),
		OK_200("200 OK"),
		CREATED_201("201 CREATED"),
		ACCEPTED_202("202 ACCEPTED"),
		NO_CONTENT_204("204 NO CONTENT"),
		PARTIAL_CONTENT("206 PARTIAL CONTENT"),
		BAD_REQUEST_400("400 BAD REQUEST"),
		UNAUTHORIZED_401("401 UNAUTHORIZED"),
		FORBIDDEN_403("403 FORBIDDEN"),
		NOT_FOUND_404("404 NOT FOUND"),		
		INTERNAL_SERVER_ERROR_500("500 INTERNAL SERVER ERROR"),
		UNIMPLEMENTED_501("501 UNIMPLEMENTED");
		
		public final String strVal;
		
		ResponseCode(String str)
		{
			this.strVal = str;
		}
	}

	/**
	 *
	 */
	public enum Field{
		HTTP_1_1("HTTP/1.1"),
		CONTENT_TYPE("Content-Type:  "),
		CONTENT_LENGTH("Content-Length: "),
		FILE_SIZE("File-Size: ");
		public final String strVal;
		Field(String str)
		{
			this.strVal = str;
		}
	}

	/**
	 *
	 */
	public enum ContentTypeVal{
		TEXT_HTML("text/html"),
		TEXT_CSS("text/css"),
		IMAGE_PNG("image/png"),
		IMAGE_JPEG("image/jpeg"),
		IMAGE_GIF("image/gif"),
		IMAGE_ICON("image/x-icon");
		
	
		public final String strVal;
		ContentTypeVal(String str)
		{
			this.strVal = str;
		}
	}
			
}
