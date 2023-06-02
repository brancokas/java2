package hr.fer.zemris.java.custom.scripting.exec;

public class ValueWrapper {
    private Object value;

    public ValueWrapper(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void add(Object incValue) {
        checkCompatibility(value, incValue);

        if (doubles(value, incValue))
            value = toDoubleValue(value) + toDoubleValue(incValue);
        else
            value = toIntValue(value) + toIntValue(incValue);
    }

    public void subtract(Object decValue) {
        checkCompatibility(value, decValue);

        if (doubles(value, decValue))
            value = toDoubleValue(value) - toDoubleValue(decValue);
        else
            value = toIntValue(value) - toIntValue(decValue);

    }

    public void multiply(Object mulValue) {
        checkCompatibility(value, mulValue);

        if (doubles(value, mulValue))
            value = toDoubleValue(value) * toDoubleValue(mulValue);
        else
            value = toIntValue(value) * toIntValue(mulValue);

    }

    public void divide(Object divValue) {
        checkCompatibility(value, divValue);

        if (doubles(value, divValue))
            value = toDoubleValue(value) / toDoubleValue(divValue);
        else
            value = toIntValue(value) / toIntValue(divValue);
    }

    public int numCompare(Object withValue) {
        checkCompatibility(value, withValue);

        if (doubles(value, withValue))
            return Double.compare(toDoubleValue(value), toDoubleValue(withValue));
        return Integer.compare(toIntValue(value), toIntValue(withValue));
    }

    private boolean doubles(Object value, Object incValue) {
        return cast(value) instanceof Double || cast(incValue) instanceof Double;
    }

    private Number cast(Object value) {
        if (value == null)
            return Integer.valueOf(0);
        if (value instanceof String) {
            String newString = (String) value;
            try {
                if (newString.contains(".") || newString.contains("E"))
                    return Double.parseDouble(newString);
                return Integer.parseInt(newString);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Given value " + value + " is not a number.");
            }
        }
        return (Number) value;
    }

    private boolean isCompatible(Object value) {
        if (value == null || value instanceof String || value instanceof Integer || value instanceof Double)
            return true;
        return false;
    }

    private void checkCompatibility(Object value, Object argument) {
        if (!isCompatible(value)) throw new RuntimeException("Value is of type: " + value.getClass());
        if (!isCompatible(argument)) throw new RuntimeException("Argument is of type: " + argument.getClass());
    }

    private int toIntValue(Object value) {
        return cast(value).intValue();
    }

    private double toDoubleValue(Object value) {
        return cast(value).doubleValue();
    }
}