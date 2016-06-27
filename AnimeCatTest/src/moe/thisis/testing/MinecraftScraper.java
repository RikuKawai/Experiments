package moe.thisis.testing;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.sun.org.apache.xerces.internal.parsers.*;

public class MinecraftScraper {

	public static void main(String[] args) throws SAXException, IOException {
		DOMParser parser = new DOMParser();
		parser.parse("assets.minecraft.net.xml");
		
		Document doc = parser.getDocument();
		
		NodeList root = doc.getChildNodes();
		
		Node main = getNode("ListBucketResult", root);
		
		String[] keys = new String[1000];
		
		for (int i=0;i<1000;i++) {
			if (i < 1) {
				Node entry = getNode("Contents", main.getChildNodes());
				NodeList nodes = entry.getChildNodes();
				keys[i] = getNodeValue("Key", nodes);
				System.out.println(keys[i] + " " + i);
			} else {
				Node entry = getNode("Contents", main.getChildNodes());
				Node entryX = entry.getNextSibling();
				NodeList nodes = entryX.getChildNodes();
				keys[i] = getNodeValue("Key", nodes);
				System.out.println(keys[i] + " " + i);
			}
			
		}
		//Node entry = main.getFirstChild();
		//NodeList nodes = entry.getChildNodes();
		//String[] key = getNodeValue("Key", nodes).split("/");
		
		for (int i=0;i<1000;i++) {
			String[] key = keys[i].split("/");
			
			System.out.println(key[0]);
			
			File dir = new File(key[0]);
			if (!dir.exists()) {
				dir.mkdir();
			}
			
			if (key.length > 1) {
				URL currentURL = new URL("http://assets.minecraft.net/" + key[0] + "/" + key[1]);
				String fileName = key[1];
				File currentFile = new File (dir, fileName);
				FileUtils.copyURLToFile(currentURL, currentFile);
				System.out.println("Saved " + currentFile);
			}
		}
		
		
		
		
		/*
		* for (int i=0; i<999; i++) {
		*	Node entry1 = main.getNextSibling();
		*	NodeList nodes2 = entry2.getChildNodes();
		*	String[] key2 = getNodeValue("Key", nodes2).split("/");
		*	
		*	File dir2 = new File(key[0]);
		*	if (!dir2.exists()) {
		*		dir2.mkdir();
		*	}
		*	
		*	if (key2.length > 1) {
		*		URL currentURL = new URL("http://assets.minecraft.net/" + key2[0] + "/" + key2[1]);
		*		String fileName = key2[1];
		*		File currentFile = new File (dir2, fileName);
		*		FileUtils.copyURLToFile(currentURL, currentFile);
		*		System.out.println("Saved " + currentFile);
		*	}
		* }
		*/
		
	}
	
	/**
	 * Get a node from the DOM
	 * @param	tagName	Tag to locate
	 * @param	nodes	Nodes to look in
	 * @return
	 */
	protected static Node getNode(String tagName, NodeList nodes) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            return node;
	        }
	    }
	 
	    return null;
	}
	/**
	 * Return the value of a DOM node
	 * @param	node
	 * @return
	 */
	protected static String getNodeValue( Node node ) {
	    NodeList childNodes = node.getChildNodes();
	    for (int x = 0; x < childNodes.getLength(); x++ ) {
	        Node data = childNodes.item(x);
	        if ( data.getNodeType() == Node.TEXT_NODE )
	            return data.getNodeValue();
	    }
	    return "";
	}
	/**
	 * Search for and return the value of a DOM node 
	 * @param	tagName	Tag to locate
	 * @param	nodes	Nodes to look in
	 * @return
	 */
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
	/**
	 * Returns node attributes
	 * @param	attrName	Attribute name
	 * @param	node
	 * @return
	 */
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
	/**
	 * Searches for a node and returns node attributes
	 * @param	tagName	Tag to locate
	 * @param	attrName	Attribute name
	 * @param	nodes
	 * @return
	 */
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
