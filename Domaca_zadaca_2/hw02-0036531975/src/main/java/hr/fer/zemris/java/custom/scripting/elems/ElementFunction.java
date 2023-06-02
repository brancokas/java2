package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;

import java.util.Objects;

/**
 * Class for storing Element
 * of type "@letter(letter+digit+_)*"
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class ElementFunction extends Element {
	private String name;
	// name starts with @ after which follows letter, and after can be letter, digits, underscore
	/**
	 * stores values of type "@letter(letter+digit+_)*"
	 * */
	public ElementFunction(String name) {
		this.name = name;
	}
	
	@Override
	public String asText() {
		return name.substring(1);
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
	 * equal if Strings of values are equal
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementFunction other = (ElementFunction) obj;
		return other.name.equals(name);
	}

	@Override
	public void accept(IncrementElements incrementElements) {
		throw new UnsupportedOperationException();
	}
}
