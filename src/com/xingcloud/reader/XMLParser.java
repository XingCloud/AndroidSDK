package com.xingcloud.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.SharedPreferences;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.utils.DbAssitant;



public class XMLParser extends DefaultHandler
{
    
    private XMLNode currentNode;
    private XMLNode rootNode;

    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        String string = new String(ch, start, length);
        if (currentNode.children.size() != 0)
        {
            XMLNode lastChild = currentNode.children.get(
                    currentNode.children.size() - 1);
            if (lastChild instanceof XMLLeafNode)
                ((XMLLeafNode)lastChild).value.append(string);
            else
                currentNode.children.add(new XMLLeafNode(currentNode, string));
        }
        else
            currentNode.children.add(new XMLLeafNode(currentNode, string));
    }
    
    public void startDocument()
		     throws SAXException
	{
    	SharedPreferences settings = XingCloud.instance().getActivity().getSharedPreferences("XingCloudSDK", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();  
		editor.putInt("dbcache", 1);
		editor.commit();
	}
    
    public void endDocument()
    		     throws SAXException
	{
    	SharedPreferences settings = XingCloud.instance().getActivity().getSharedPreferences("XingCloudSDK", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();  
		editor.putInt("dbcache", 2);
		editor.commit();
	}

    // DefaultHandler
    
    public XMLNode parse(InputStream xmlStream)
            throws SAXException, ParserConfigurationException, IOException
    {
        rootNode = new XMLNode();
        currentNode = rootNode;
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser(); 
        XMLReader xmlReader = parser.getXMLReader(); 
       
        xmlReader.setContentHandler(this);
        xmlReader.parse(new InputSource(xmlStream)); 
        return rootNode;
    }

    public XMLNode parse(String xml)
            throws SAXException, ParserConfigurationException, IOException
    {
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        return parse(xmlStream);
    }

    public void startElement(String uri, String localName, String qName,
            Attributes atts)
            throws SAXException
    {
        Map<String, String> attributes = new HashMap<String, String>();
        String attsArray = "";
        for (int i = 0; i < atts.getLength(); ++i)
        {
        	attributes.put(atts.getLocalName(i), atts.getValue(i));
        	if(i == 0)
        	{
        		attsArray += atts.getLocalName(i)+"="+atts.getValue(i);
        	}
        	else
        	{
        		attsArray += DbAssitant.dbItemAttributeSplit + atts.getLocalName(i)+"="+atts.getValue(i);
        	}
        }
            
        XMLInternalNode node = new XMLInternalNode(currentNode, localName,
                attributes);

        storeDataToDb(node,attsArray);

        currentNode.children.add(node);
        currentNode = node;
        attsArray = null;
        node = null;
    }
    
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        currentNode = currentNode.parent;
    }

    public void storeDataToDb(XMLInternalNode node, String attsArray)
    {
    	if(node.isGroup())
    	{
    		String childs = null;
    		if(null != currentNode.id && currentNode.id.trim().length()>0)
    			childs= DbAssitant.instance().getChildGroupsByIdReturnString(currentNode.id);
    		if(null == childs || childs.trim().length() <=0)
    		{
    			
    			childs= "";
    			childs += node.id;
    			if(null != currentNode.id && currentNode.id.trim().length()>0 )
    			{
    				if(null != currentNode.parent.id && currentNode.parent.id.trim().length()>0)
    				{
    					DbAssitant.instance().insertGroup(currentNode.id, currentNode.parent.id, childs);
    				}
    				else
    				{
    					DbAssitant.instance().insertGroup(currentNode.id, "topRoot", childs);
    				}
    			}
    			else
    			{
    				DbAssitant.instance().insertGroup("topRoot", "top", childs);
    			}
    		}
    		else
    		{
    			if(!childs.contains(node.id))
    			childs +=(","+node.id);
    			if(null != currentNode.id && currentNode.id.trim().length()>0)
    			{
    				if(null != currentNode.parent.id && currentNode.parent.id.trim().length()>0)
    				{
    					DbAssitant.instance().insertGroup(currentNode.id, currentNode.parent.id, childs);
    				}
    				else
    				{
    					DbAssitant.instance().insertGroup(currentNode.id, "topRoot", childs);
    				}
    			}
    			else
    			{
    				DbAssitant.instance().insertGroup("topRoot", "top", childs);
    			}
    		}
    		childs = null;
    		
    	}
    	else if(node.isItemSpec())
    	{
//    		if(!DbAssitant.instance().isItemIn(node.id))
//    		{
//    			if(null != currentNode.id && currentNode.id.trim().length()>0) 
//    				DbAssitant.instance().insertItem(node.id, currentNode.id, attsArray);
//    		}
//    		else
    		//{
    			if(null != currentNode.id && currentNode.id.trim().length()>0) 
    				DbAssitant.instance().insertItem(node.id, currentNode.id, attsArray);
    		//}
    	}
    }
    
}