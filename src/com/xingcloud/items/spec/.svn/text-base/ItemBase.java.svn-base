package com.xingcloud.items.spec;

import org.w3c.dom.Node;

/**
 * itemSpec和itemGroup的共同基类
 * */
public class ItemBase extends AsObject {
	/**描述，不同语种可能会不一样**/
	protected String description;
	/**唯一标志，必须**/
	protected String id;
	/**名字，不同语种可能不一样**/
	protected String name;
	/**XML源定义**/
	protected Node node;
	/**父亲**/
	protected ItemGroup parent;

	public String getDescription() {
		return description;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Node getNode() {
		return node;
	}
	public ItemGroup getParent() {
		return parent;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public void setParent(ItemGroup parent) {
		this.parent = parent;
	}

}
