package com.xingcloud.reader;

import java.util.Map;

public class XMLInternalNode extends XMLNode
{
    public Map<String, String> attributes;
    public String key;

    public XMLInternalNode(XMLNode parent, String key,
            Map<String, String> attributes)
    {
        super(parent);
        
        this.key = key;
        this.attributes = attributes;
        this.id = attributes.get("id");
    }
    
    public String generateXML()
    {
        StringBuilder attrsString = new StringBuilder();
        if (attributes != null)
        {
            for (String name : attributes.keySet())
                attrsString.append(String.format(" %s=\"%s\"", name,
                        attributes.get(name)));
        }
        return String.format("<%s%s>%s</%s>", key, attrsString.toString(),
                super.generateXML(), key);
    }
    public Boolean isGroup()
    {
    	return this.key.contains("Group")?true:false;
    }
    public Boolean isItemSpec()
    {
    	return this.key.contains("ItemSpec")?true:false;
    }
}
