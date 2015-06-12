package hbadhani.webserver.http;

/**
 * This class represents an HTTP request
 */
public class HttpRequest {

	public RequestType type;
	public String url;
	/**
	 * This enum describes the different HTTP request types.
	 */
	public enum RequestType{
		GET("GET"),
		HEAD("HEAD"),
		OPTIONS("OPTIONS"),
		POST("POST"),
		PUT("PUT"),
		UNKNOWN("UNKNOWN");
		public final String strVal;
		RequestType(String str)
		{
			this.strVal = str;
		}
	}
}
