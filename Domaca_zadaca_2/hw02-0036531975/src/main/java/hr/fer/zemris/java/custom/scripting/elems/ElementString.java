package hr.fer.zemris.java.custom.scripting.elems;


import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

import java.util.Objects;
/**
 * Class for storing Element
 * of type String with double-qoutes 
 * in beginning and end
 * Valid charachters to escape are:
 * \, ", n, t, r
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class ElementString extends Element {
	private String value;
	// stars and ends with double qoutes " name "
	// \\ = \; \" = "; \n, \t, \r - part of tags
	/**
	 * stores element type string  with double-qoutes 
	 * in beginning and end
	 * Valid charachters to escape are:
	 * \, ", n, t, r
	 * */
	public ElementString(String value) {
		for (int i = 0, size = value.length(); i < size; i++) {
			if (value.charAt(i) == '\\' && i == size-2) 
				throw new SmartScriptParserException("Wrong escape in String");
			if (value.charAt(i) == '\\') {
				i++;
			}
		}
		
		this.value = value;
	}

	@Override
	public String asText() {
		return value.substring(1,value.length()-1);
	}
	
	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	/**
	 * equal if Strings are the same
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementString other = (ElementString) obj;
		return other.value.equals(value);
	}

	@Override
	public void accept(IncrementElements incrementElements) {
		incrementElements.addString(value);
	}
}
