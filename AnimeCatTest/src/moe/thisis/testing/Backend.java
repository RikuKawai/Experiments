package moe.thisis.testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import de.comhix.anidb.anidb4j.*;

public class Backend {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("AnimeCatJ v0.0.1a");
		System.out.println("Enter MAL Username: ");
		String username = in.readLine();
		System.out.println("Enter MAL Password: ");
		String password = in.readLine();
		System.out.println("");
		System.out.println("Enter Search: ");
		String query = in.readLine();
		
		printData(username, password, query);
		
	}
		public static void printData(String username, String password, String query) throws MalformedURLException {
		//String uri = "http://myanimelist.net/api/anime/search.xml"
			Authenticator.setDefault(new Authenticator() {
				 @Override
				        protected PasswordAuthentication getPasswordAuthentication() {
				         return new PasswordAuthentication(
				   username, password.toCharArray());
				        }
				});
			URL url = new URL("http://myanimelist.net/api/anime/search.xml?q=" + formatQuery(query));
		try {
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(url.openStream()));
			Document doc = parser.getDocument();
			
			NodeList root = doc.getChildNodes();
			
			Node anime = getNode("anime", root);
			Node entry = getNode("entry", anime.getChildNodes());
			
			NodeList nodes = entry.getChildNodes();
			String id = getNodeValue("id", nodes);
			String title = getNodeValue("english", nodes);
			String episodes = getNodeValue("episodes", nodes);
			String score = getNodeValue("score", nodes);
			String status = getNodeValue("status", nodes);
			String startDate = getNodeValue("start_date", nodes);
			String image = getNodeValue("image", nodes);
			
			System.out.println();
			System.out.println("Search Result:");
			System.out.println("ID: " + id);
			System.out.println("English Title: " + title);
			System.out.println("Episode Count: " + episodes);
			System.out.println("Average Score: " + score);
			System.out.println("Airing Status: " + status);
			System.out.println("Starting Date: " + startDate);
			System.out.println("Image URL: " + image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String formatQuery(String query) {
		String formattedQuery = query.replaceAll(" ", "+");
		return formattedQuery;
	}
	protected static Node getNode(String tagName, NodeList nodes) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            return node;
	        }
	    }
	 
	    return null;
	}
	 
	protected static String getNodeValue( Node node ) {
	    NodeList childNodes = node.getChildNodes();
	    for (int x = 0; x < childNodes.getLength(); x++ ) {
	        Node data = childNodes.item(x);
	        if ( data.getNodeType() == Node.TEXT_NODE )
	            return data.getNodeValue();
	    }
	    return "";
	}
	 
	protected static String getNodeValue(String tagName, NodeList nodes ) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            NodeList childNodes = node.getChildNodes();
	            for (int y = 0; y < childNodes.getLength(); y++ ) {
	                Node data = childNodes.item(y);
	                if ( data.getNodeType() == Node.TEXT_NODE )
	                    return data.getNodeValue();
	            }
	        }
	    }
	    return "";
	}
	 
	protected static String getNodeAttr(String attrName, Node node ) {
	    NamedNodeMap attrs = node.getAttributes();
	    for (int y = 0; y < attrs.getLength(); y++ ) {
	        Node attr = attrs.item(y);
	        if (attr.getNodeName().equalsIgnoreCase(attrName)) {
	            return attr.getNodeValue();
	        }
	    }
	    return "";
	}
	 
	protected static String getNodeAttr(String tagName, String attrName, NodeList nodes ) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            NodeList childNodes = node.getChildNodes();
	            for (int y = 0; y < childNodes.getLength(); y++ ) {
	                Node data = childNodes.item(y);
	                if ( data.getNodeType() == Node.ATTRIBUTE_NODE ) {
	                    if ( data.getNodeName().equalsIgnoreCase(attrName) )
	                        return data.getNodeValue();
	                }
	            }
	        }
	    }
	 
	    return "";
	}
}
