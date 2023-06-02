package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.elems.Element;

public interface IncrementElements {
    void addInteger(Integer element);
    void addDouble(Double element);
    void addString(String element);
    Element get();

}
