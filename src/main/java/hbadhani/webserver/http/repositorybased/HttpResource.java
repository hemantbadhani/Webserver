package hbadhani.webserver.http.repositorybased;

/**
 *  This class represents a resource, requested by an HTTP request
 */
public class HttpResource {

	String name;
	String value;
	String type;

	/**
	 * This enum describes the different resource types supported by .
	 */
	public enum ResourceType{
		HTML_TEXT("HTML_TEXT"),
		IMAGE_PNG("IMAGE_PNG"),
		PHP("PHP"),
		DIR("DIR"),
		FILE("FILE");
		public final String strVal;
		ResourceType(String str)
		{
			this.strVal = str;
		}
	}
}
