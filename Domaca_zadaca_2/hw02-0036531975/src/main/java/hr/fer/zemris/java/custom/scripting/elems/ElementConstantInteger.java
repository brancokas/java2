package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;

import java.util.Objects;


/**
 * Class for storing Element
 * of type Integer
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class ElementConstantInteger extends Element {
	private int value;

	public int getValue() {
		return value;
	}

	/**
	 * Store positive and negative Integers
	 * */
	public ElementConstantInteger(int value) {
		this.value = value;
	}
	
	@Override
	public String asText() {
		return String.valueOf(value);
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	/**
	 * equal if values of Element are equal
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementConstantInteger other = (ElementConstantInteger) obj;
		return value == other.value;
	}

	@Override
	public void accept(IncrementElements incrementElements) {
		incrementElements.addInteger(value);
	}
}
