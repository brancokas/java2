package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.oprpp1.custom.collections.ObjectStack;
import hr.fer.oprpp1.custom.scripting.lexer.SmartLexer;
import hr.fer.oprpp1.custom.scripting.lexer.Token;
import hr.fer.oprpp1.custom.scripting.lexer.TokenType;
import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;

/**
 * class SmartScriptParser checks the
 * validity of order in text of the
 * given syntax
 * Returns the whole text as a 
 * Document Node with all of its children
 * 
 * @author bstankovic
 * @version 1.0

 * */
public class SmartScriptParser {
	private SmartLexer lexer;
	private ObjectStack stack;
	
	/**
	 * constructor creates a new lexer
	 * */
	public SmartScriptParser(String text) {
		lexer = new SmartLexer(text);
	}
	
	/**
	 * @return Element of the current token from the text
	 * */
	private Element getMyElement(Token token) {
		if (token.getType() == TokenType.DOUBLE) {
			return new ElementConstantDouble(Double.valueOf(token.getElement().toString()));
		}
		if (token.getType() == TokenType.INTEGER) {
			return new ElementConstantInteger(Integer.valueOf(token.getElement().toString()));
		}
		if (token.getType() == TokenType.VARIABLE) {
			return new ElementVariable(token.getElement().toString());
		}
		if (token.getType() == TokenType.FUNCTION) {
			return new ElementFunction(token.getElement().toString());
		}
		if (token.getType() == TokenType.STRING) {
			return new ElementString(token.getElement().toString());
		}
		return new ElementOperator(token.getElement().toString());
	}
	
	/**
	 * checks the validty of order of elements in for tag
	 * and adds it to the stack
	 * @throws SmartScriptParserException if too few or many arguments in for tag
	 * 			or tag isn't closed or first element isn't a variable
	 * */
	private void forTag() {
		Token token = lexer.getNextToken();
		int index = 0;
		Element[] elements = new Element[4];
		
		while (token.getType() != TokenType.EOF && token.getType() != TokenType.TAG_END) {
			if (index == 4) throw new SmartScriptParserException("Too many arguments in For-tag");
			
			elements[index++] = getMyElement(token);
			
			token = lexer.getNextToken();
		}
		if (token.getType() == TokenType.EOF) 
			throw new SmartScriptParserException("Didn't close tag");
		
		if (index < 3)
			throw new SmartScriptParserException("Too few arguments in For-tag");
		if (elements[0] instanceof ElementVariable) {
			Node n = (Node)stack.peek();
			n.addChildren(new ForLoopNode(elements));
			stack.push(n.getChild(n.numberOfChildren()-1));
		} else		
			throw new SmartScriptParserException("First element must be a variable");
	}
	
	/**
	 * creates echonode with all of it's elements in it
	 * direct child of the last Node from the stack
	 * @throws SmartScriptParserException if tag isn't closed
	 * */
	private void equalTag() {
		Token token = lexer.getNextToken();
		int index = 0;
		Element[] elements = new Element[16];
		
		while(token.getType() != TokenType.EOF && token.getType() != TokenType.TAG_END) {
			if (index == elements.length) {
				Element[] newElements = new Element[index*2];
				for (int i = 0; i < index; i++) {
					newElements[i] = elements[i];
				}
				elements = newElements;
			}
			elements[index++] = getMyElement(token);
			
			token = lexer.getNextToken();
		}
		
		if (token.getType() == TokenType.EOF) 
			throw new SmartScriptParserException("Didn't close Equal-tag");
		
		Node n = (Node)stack.peek();
		n.addChildren(new EchoNode(elements));
		
	}
	
	/**
	 * removes the last elements on stack
	 * @throws SmartScriptParserException if tag isn't closed
	 * 			or there are too many end tags in respect to for tags
	 * */
	private void endTag() {
		if (lexer.getNextToken().getType() != TokenType.TAG_END)
			throw new SmartScriptParserException("Didn't close End-tag");
		
		stack.pop();
		
		if (stack.isEmpty()) 
			throw new SmartScriptParserException("Too many end-tags");
	}
	
	/**
	 * indentifies which tag is currently being processed
	 * @throws SmartScriptParserException if first input isn't for,end,=
	 * */
	private void resolveTag() {
		Token token = lexer.getNextToken();

		if (token.getType() == TokenType.FOR) {
			forTag();
		} else if (token.getType() == TokenType.EQUAL) {
			equalTag();
		} else if (token.getType() == TokenType.END) {
			endTag();
		} else {
			throw new SmartScriptParserException("Wrong first input in tag");
		}
	}

	/**
	 * @return the root of the document as DocumentNode
	 * 		with all the Nodes as it's children
	 * @throws SmartScriptParserException if there are uncorrectly closed for tags
	 * */
	public DocumentNode getDocumentNode() {
		DocumentNode docNode = new DocumentNode();
		stack = new ObjectStack();
		stack.push(docNode);
		
		while (lexer.getNextToken().getType() != TokenType.EOF) {
			TokenType currentType = lexer.getCurrentToken().getType();
			
			if (currentType == TokenType.TEXT) {
				Node node = (Node)stack.peek();
				node.addChildren(new TextNode(lexer.getCurrentToken().getElement().toString()));
			}
			if (currentType == TokenType.TAG_START) {
				resolveTag();
			}
		}
		
		if (stack.size() != 1) 
			throw new SmartScriptParserException("Didn't close For-tag");
		DocumentNode n = (DocumentNode)stack.pop();
		
		return n;
	}
}
