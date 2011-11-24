package com.xingcloud.reader;

public class XMLLeafNode extends XMLNode
{
    public StringBuilder value;

    public XMLLeafNode(XMLNode parent, String value)
    {
        super(parent);
        
        this.value = new StringBuilder(value);
    }

    public String generateXML()
    {
        return value.toString();
    }
}
