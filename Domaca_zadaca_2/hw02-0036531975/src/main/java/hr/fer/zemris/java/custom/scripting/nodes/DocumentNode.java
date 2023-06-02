package hr.fer.zemris.java.custom.scripting.nodes;

import java.io.IOException;

/**
 * Class Document Node represents
 * top node of a file
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class DocumentNode extends Node {
	
	/**
	 * @return String of the whole document 
	 * */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

//		for (int i = 0, size = this.numberOfChildren(); i < size; i++) {
//			sb.append(this.getChild(i).toString());
//		}

		return sb.toString();
	}
	
	/**
	 * @return String of the whole document where 
	 * 			each Node is correctly indented
	 * */
	public String toStringDocumentIndented() {
		StringBuilder sb = new StringBuilder("Document Node:\n");
		
		this.incrementIndent();
		for (int i = 0, size = this.numberOfChildren(); i < size; i++) {
			sb.append(this.getChild(i).toStringIndented(String.valueOf(i) + ": ") + '\n');
		}
		this.decrementIndent();
		return sb.toString();
	}

	@Override
	public void accept(INodeVisitor iNodeVisitor) throws IOException {
		iNodeVisitor.visitDocumentNode(this);
//		int size = numberOfChildren();
//		for (int i = 0; i < size; i++) {
//			getChild(i).accept(iNodeVisitor);
//		}
	}

	/**
	 * @return <code>true</code> if each element in document 
	 * 			is equal to other
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		DocumentNode other = (DocumentNode) obj;
		
		if (other.numberOfChildren() != numberOfChildren()) return false;
		
		for (int i = 0, size = numberOfChildren(); i < size; i++) {
			if (!(other.getChild(i).equals(getChild(i)))) return false;
		}
		return true;
	}
}
