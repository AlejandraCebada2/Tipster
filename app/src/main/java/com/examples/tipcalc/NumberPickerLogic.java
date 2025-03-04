package com.examples.tipcalc;
import android.widget.EditText;

public class NumberPickerLogic {
    int minimum = 0;
    int maximum = Integer.MAX_VALUE;
    EditText number;

    public NumberPickerLogic(EditText number) {
        this(number, 0, Integer.MAX_VALUE);
    }

    public NumberPickerLogic(EditText number, int minimum, int maximum) {
        super();
        this.number = number;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public void increment() {
        final int newValue = clamp(getValue() + 1);
        setValue(newValue);
    }

    public void decrement() {
        final int newValue = clamp(getValue() - 1);
        setValue(newValue);
    }

    /** Ensure that the value to be set falls within the allowable range */
    int clamp(int newValue) {
        if (newValue < minimum) {
            newValue = minimum;
        }
        if (newValue > maximum) {
            newValue = maximum;
        }
        return newValue;
    }

    /** Return the integer value of the clicker. */
    public int getValue() {
        String text = number.getText().toString();
        if (text.isEmpty()) {
            return 0; // O cualquier valor predeterminado que tenga sentido para tu aplicación
        } else {
            return Integer.parseInt(text);
        }
    }


    /** Force the value */
    public void setValue(int value) {
        number.setText(Integer.toString(value));
    }
}
