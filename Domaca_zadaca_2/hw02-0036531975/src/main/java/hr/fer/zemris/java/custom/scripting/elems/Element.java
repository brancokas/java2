package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;

import java.util.Comparator;

/**
 * Abstract class Element
 * gives a blueprint that 
 * each element can be 
 * represented as String by
 * asText method because it
 * contains only one value
 * 
 * @author bstankovic
 * @version 1.0
 * */
abstract public class Element {

	/**
	 * @return String value of an element
	 * */
	public String asText() {
		return "";
	}

	abstract public void accept(IncrementElements incrementElements);
	
}
