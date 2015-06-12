package hbadhani.webserver.interfaces;


public interface ReqProcessorFactory extends Runnable{

		ReqProcessor getReqProcessor(Connection connection);
		
}
