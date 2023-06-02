package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

import java.io.IOException;

/**
 * class Node stores Nodes of document
 * 
 * @author bstankovic
 * @version 1.0
 * */
abstract public class Node {
	private ArrayIndexedCollection array;
	private static int indent = 0;
	/**
	 * make an instance of array only when addChildren is called for the first time
	 * adds child to end of the array
	 * */
	public void addChildren(Node child) {
		if (array == null) {
			array = new ArrayIndexedCollection();
		}
		this.array.add(child);
	}
	
	/**
	 * @return number of direct children of that node
	 * */
	public int numberOfChildren() {
		if (array == null) return 0;
		return array.size();
	}
	
	/**
	 * @return Node at index from [0...size>
	 * @throws SmartScriptParserException if index is out of array bounds
	 * */
	public Node getChild(int index) {
		if (index < 0 || index >= array.size()) 
			throw new SmartScriptParserException("Index out of bounds");
		
		return (Node)array.get(index);
	}
	
	/**
	 * increment indent by one
	 * */
	public void incrementIndent() {
		indent++;
	}

	/**
	 * decrement indent by one
	 * */
	public void decrementIndent() {
		indent--;
	}
	
	/**
	 * @return String of current Node indented in respect 
	 * 			to other Nodes
	 * */
	public String toStringIndented(String iteration) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < indent; i++) {
			sb.append('\t');
			if (i == indent-1) {
				sb.append(' ');
			}
		}
		
		return sb.toString() + iteration + toStringDocumentIndented();
	}
	
	/**
	 * abstract method
	 * @return String of a Node that is indented 
	 * */
	abstract public String toStringDocumentIndented();

	abstract public void accept(INodeVisitor iNodeVisitor) throws IOException;
	
}
