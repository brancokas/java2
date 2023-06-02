package hr.fer.zemris.java.custom.scripting.nodes;

import java.io.IOException;

/**
 * class TextNode represents
 * everything that is not inside of tags
 * Can escape only \ and {, everything else
 * is considered invalid
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class TextNode extends Node {
	private String text;
	//allowed only \\=\ and \{={
	public TextNode(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	/**
	 * @return indented String of the text node content
	 * */
	@Override
	public String toStringDocumentIndented() {
		return "Text Node: " + text;
	}

	@Override
	public void accept(INodeVisitor iNodeVisitor) throws IOException {
		iNodeVisitor.visitTextNode(this);
	}

	/**
	 * @return String like-wise from the original text
	 * */
	@Override
	public String toString() {
		return text;
	}
	
	/**
	 * @return <code>true</code> if String of text node is 
	 * 			equal to other
	 * */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TextNode)) return false;
		
		TextNode node = (TextNode)obj;
		
		if (node.getText().equals(this.text)) return true;
		return false;
		
	}
}
