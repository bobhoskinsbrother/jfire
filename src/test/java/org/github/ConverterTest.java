package org.github;

import org.github.converter.Converter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConverterTest {

    private final Converter unit = new Converter(new HashMap<>());

    @Test
    void canConvertTrueBoolean() {
        Object reply = unit.convert("true", Boolean.TYPE);
        assertThat(reply, is(true));
    }

    @Test
    void canConvertTrueBooleanUpper() {
        Object reply = unit.convert("TRUE", Boolean.TYPE);
        assertThat(reply, is(true));
    }

    @Test
    void canConvertFalseBoolean() {
        Object reply = unit.convert("false", Boolean.TYPE);
        assertThat(reply, is(false));
    }

    @Test
    void canConvertFalseBooleanUpper() {
        Object reply = unit.convert("FALSE", Boolean.TYPE);
        assertThat(reply, is(false));
    }

    @Test
    void cannotConvertYes() {
        assertThrows(IllegalArgumentException.class, () -> {
            unit.convert("yes", Boolean.TYPE);
        });
    }

    @Test
    void canConvertNullBoolean() {
        Object reply = unit.convert("null", Boolean.class);
        assertThat(reply, is(nullValue()));
    }

    @Test
    void canConvert1ToInt() {
        Object reply = unit.convert("1", Integer.TYPE);
        assertThat(reply, is(1));
    }

    @Test
    void canConvert1ToInteger() {
        Object reply = unit.convert("1", Integer.class);
        assertThat(reply, is(1));
    }

    @Test
    void canConvertNullToInteger() {
        Object reply = unit.convert("null", Integer.class);
        assertThat(reply, is(nullValue()));
    }

    @Test
    void cannotConvertOneToInteger() {
        assertThrows(IllegalArgumentException.class, () -> {
            unit.convert("one", Integer.class);
        });
    }

    @Test
    void canConvert1To_long() {
        Object reply = unit.convert("1", Long.TYPE);
        assertThat(reply, is(1L));
    }

    @Test
    void canConvert1ToLong() {
        Object reply = unit.convert("1", Long.class);
        assertThat(reply, is(1L));
    }

    @Test
    void canConvertNullToLong() {
        Object reply = unit.convert("null", Long.class);
        assertThat(reply, is(nullValue()));
    }

    @Test
    void cannotConvertOneToLong() {
        assertThrows(IllegalArgumentException.class, () -> {
            unit.convert("one", Long.class);
        });
    }

    @Test
    void canConvertNullToString() {
        Object reply = unit.convert("null", String.class);
        assertThat(reply, is(nullValue()));
    }

    @Test
    void canConvertFredToString() {
        Object reply = unit.convert("fred", String.class);
        assertThat(reply, is("fred"));
    }

    @Test
    void canConvert1_0To_double() {
        Object reply = unit.convert("1.0", Double.TYPE);
        assertThat(reply, is(1.0d));
    }

    @Test
    void canConvert1_0ToDouble() {
        Object reply = unit.convert("1.0", Double.class);
        assertThat(reply, is(1.0d));
    }

    @Test
    void canConvertNullToDouble() {
        Object reply = unit.convert("null", Double.class);
        assertThat(reply, is(nullValue()));
    }

    @Test
    void cannotConvertOneToDouble() {
        assertThrows(IllegalArgumentException.class, () -> {
            unit.convert("one.zero", Double.class);
        });
    }

    @Test
    void cannotConvertConverter() {
        assertThrows(IllegalArgumentException.class, () -> {
            unit.convert("fred", Converter.class);
        });
    }


}
