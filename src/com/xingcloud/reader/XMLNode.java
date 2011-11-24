package com.xingcloud.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class XMLNode
{
    
    public List<XMLNode> children = new ArrayList<XMLNode>();
    public String id;
    public XMLNode parent;
    public XMLNode()
    {
    }
    
    public XMLNode(XMLNode parent)
    {
        this.parent = parent;
    }
    
    /* For each (key, value) in the map XMLInternalNode with key of
     * key.toString() is created. If value is a map, children are added to that
     * node recursively using this method. Otherwise, XMLLeafNode is added with
     * a value of value.toString().
     */
    public void addChildren(Map<?, ?> newChildren)
    {
        for (Object key : newChildren.keySet())
        {
            XMLInternalNode node = new XMLInternalNode(this, key.toString(),
                    null);
            children.add(node);
            Object value = newChildren.get(key);
            if (value instanceof Map<?, ?>)
                node.addChildren((Map<?, ?>)value);
            else
            {
                XMLLeafNode leaf = new XMLLeafNode(node, value.toString());
                node.children.add(leaf);
            }
        }
    }

    public String generateXML()
    {
        StringBuilder result = new StringBuilder();
        if (parent == null)
            result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        for (XMLNode child : children)
            result.append(child.generateXML());
        return result.toString();
    }
    
    public Boolean parentIsRoot(XMLNode rootNode)
    {
    	if(this.parent.equals(rootNode))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    /* Descends down the tree according to path specified in a query of the
     * form: "NodeKey/SubNodeKey/SubSubNodeKey". At each level the first child
     * with a given key is chosen (additionally, * compares equal to every key).
     * If the query contains trailing slash /, the first leaf is selected at the
     * end. Returns the final node or null if such path does not exist.
     */
    public XMLNode queryNode(String query)
    {
        String[] queryTokens;
        if (query.endsWith("/"))
        {
            queryTokens = query.concat(" ").split("/");
            queryTokens[queryTokens.length - 1] = "";
        }
        else
            queryTokens = query.split("/");
        return queryNode(queryTokens, 0, queryTokens.length - 1);
    }

    /* Same as queryNode(String). queryTokens contains node keys (with the last
     * one possibly empty) and querying begins and ends at given indices.
     */
    private XMLNode queryNode(String[] queryTokens, int firstIndex,
            int lastIndex)
    {
        String key = queryTokens[firstIndex];
        if (key.length() == 0)
        {
            for (XMLNode child : children)
                if (child instanceof XMLLeafNode)
                    return child;
        }
        else
        {
            for (XMLNode child : children)
                if (child instanceof XMLInternalNode
                        && (key.equals(((XMLInternalNode)child).key)
                        || key.equals("*")))
                {
                    if (firstIndex == lastIndex)
                        return child;
                    return child.queryNode(queryTokens, firstIndex + 1,
                            lastIndex);
                }
        }
        return null;
    }

    /* Descends down the tree using queryNode(List<String>, int, int) for the
     * given query without the last hop. If such path does not exist, returns
     * null. Otherwise returns an array (possibly empty) with all nodes matching
     * the last hop.
     */ 
    public List<XMLNode> queryNodes(String query)
    {
        String[] queryTokens;
        if (query.endsWith("/"))
        {
            queryTokens = query.concat(" ").split("/");
            queryTokens[queryTokens.length - 1] = "";
        }
        else
            queryTokens = query.split("/");
        XMLNode node = (queryTokens.length > 1
                ? queryNode(queryTokens, 0, queryTokens.length - 2)
                : this);
        if (node == null)
            return null;
        List<XMLNode> result = new ArrayList<XMLNode>();
        String key = queryTokens[queryTokens.length - 1];
        if (key.length() == 0)
        {
            for (XMLNode child : node.children)
                if (child instanceof XMLLeafNode)
                    result.add(child);
        }
        else
        {
            for (XMLNode child : node.children)
                if (child instanceof XMLInternalNode
                        && (key.equals(((XMLInternalNode)child).key)
                        || key.equals("*")))
                {
                    result.add(child);
                }
        }
        return result;
    }
}
