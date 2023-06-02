package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.exec.IncrementElements;

import java.util.Comparator;
import java.util.Objects;


/**
 * Class for storing Element
 * of type Double
 * 
 * @author bstankovic
 * @version 1.0
 * */
public class ElementConstantDouble extends Element {
	public double getValue() {
		return value;
	}

	private double value;
	// can be negative; valid form (-)digits+.digits+
	/**
	 * stores positive and negative doubles digit-dot-digit format
	 * */
	public ElementConstantDouble(double value) {
		this.value = value;
	}
	
	@Override
	public String asText() {
		return String.valueOf(value);
	}

	@Override
	public void accept(IncrementElements incrementElements) {
		incrementElements.addDouble(value);
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
	 * equal if values in Element equal
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementConstantDouble other = (ElementConstantDouble) obj;
		return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
	}


}
