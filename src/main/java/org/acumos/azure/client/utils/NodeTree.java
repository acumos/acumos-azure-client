package org.acumos.azure.client.utils;
import java.util.ArrayList;
import java.util.List;

public class NodeTree<T> {
 
	private T data = null;
 
	private List<NodeTree<T>> children = new ArrayList<>();
 
	private NodeTree<T> parent = null;
 
	public NodeTree(T data) {
		this.data = data;
	}
 
	public NodeTree<T> addChild(NodeTree<T> child) {
		child.setParent(this);
		this.children.add(child);
		return child;
	}
 
	public void addChildren(List<NodeTree<T>> children) {
		children.forEach(each -> each.setParent(this));
		this.children.addAll(children);
	}
 
	public List<NodeTree<T>> getChildren() {
		return children;
	}
 
	public T getData() {
		return data;
	}
 
	public void setData(T data) {
		this.data = data;
	}
 
	private void setParent(NodeTree<T> parent) {
		this.parent = parent;
	}
 
	public NodeTree<T> getParent() {
		return parent;
	}
 
}