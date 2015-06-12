package hbadhani.webserver.http.repositorybased;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HttpResourceRepository {
	
	private static Map<String,HttpResource> resourceMap = new HashMap<String,HttpResource>();
	private static Map<String,HttpResource> dbResourceMap = new HashMap<String,HttpResource>();/*for Type="DB"*/
	
	/**From http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser
	 * */	 
	public static void loadResourceMapFromXML() {		
		    try {
		 
			File fXmlFile = new File("content/ResourceMap.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
		 
			 
			NodeList nList = doc.getElementsByTagName("resource");
		 
		
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		  
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					HttpResource httpResource = new HttpResource();
					
					httpResource.type  =  eElement.getAttribute("type");
					httpResource.name  =  eElement.getElementsByTagName("name").item(0).getTextContent();
					httpResource.value =  eElement.getElementsByTagName("value").item(0).getTextContent();
					if(httpResource.type.equalsIgnoreCase("DB")){
						dbResourceMap.put(httpResource.name, httpResource);	
					}
					else{
						resourceMap.put(httpResource.name, httpResource);	
					}
					
		 
				}
			}
		    }
		    catch (Exception e) {
			e.printStackTrace();
		    }
	}
	public static HttpResource getResource(String name)
	{
		return resourceMap.get(name);		
	}
	
	public static HttpResource getResource(String name,String type)	{
		if(type.equalsIgnoreCase("DB")){
			return dbResourceMap.get(name);
		}
		else{
			return resourceMap.get(name);
		}
		
	}
	
}

