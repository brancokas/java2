package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;

import java.util.Objects;
/**
 * Class for storing Element
 * of type String represented as:
 * letter(letter+digit+_)*
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class ElementVariable extends Element {
	private String name;
	// name starts with letter, after that can be letter, number, or underscore
	/**
	 * stores strings of type: letter(letter+digit+_)*
	 * */
	public ElementVariable(String name) {
		this.name = name;
	}
	
	@Override
	public String asText() {
		return name;
	} 
	
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	/**
	 * equal if strings are the same
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementVariable other = (ElementVariable) obj;
		return other.name.equals(name);
	}

	@Override
	public void accept(IncrementElements incrementElements) {
		throw new UnsupportedOperationException();
	}
}
