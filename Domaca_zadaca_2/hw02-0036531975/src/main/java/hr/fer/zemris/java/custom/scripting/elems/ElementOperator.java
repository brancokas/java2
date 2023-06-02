package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;

import java.util.Objects;
/**
 * Class for storing Element
 * operators such as:
 * +, -, *, /, ^
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class ElementOperator extends Element {
	private String symbol;
	// valid operators are: + - * / ^
	/**
	 * stores operators type:
	 * +,-,*,/,^
	 * */
	public ElementOperator(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String asText() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return this.symbol;
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbol);
	}

	/**
	 * equal if operators are the same
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementOperator other = (ElementOperator) obj;
		return other.symbol.equals(symbol);
	}


	@Override
	public void accept(IncrementElements incrementElements) {
		throw new UnsupportedOperationException();
	}
	
	
}
