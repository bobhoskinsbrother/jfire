package org.github.converter;

import java.util.HashMap;
import java.util.Map;

public class Converter {

    private final Map<Class<?>, TypeConverter> converters;

    public Converter(Map<Class<?>, TypeConverter> additionalConverters) {
        converters = addConverters(additionalConverters);
    }

    public Object convert(String input, Class<?> type) {
        if ("null".equalsIgnoreCase(input) && !type.isPrimitive()) {
            return null;
        }
        TypeConverter converter = findConverter(type);
        return converter.convert(input);
    }

    private TypeConverter findConverter(Class<?> type) {
        final TypeConverter reply = converters.get(type);
        if (reply == null) {
            throw new IllegalArgumentException("Unable to convert " + type.getName());
        }
        return reply;
    }

    private Map<Class<?>, TypeConverter> addConverters(Map<Class<?>, TypeConverter> additionalConverters) {
        final HashMap<Class<?>, TypeConverter> map = new HashMap<>();
        map.put(Boolean.TYPE, booleanConverter());
        map.put(Boolean.class, booleanConverter());
        map.put(Integer.TYPE, Integer::parseInt);
        map.put(Integer.class, Integer::parseInt);
        map.put(Long.TYPE, Long::parseLong);
        map.put(Long.class, Long::parseLong);
        map.put(Double.TYPE, Double::parseDouble);
        map.put(Double.class, Double::parseDouble);
        map.put(String.class, value->value);
        map.putAll(additionalConverters);
        return map;
    }

    private TypeConverter booleanConverter() {
        return (value) -> {
            if ("true".equalsIgnoreCase(value)) {
                return true;
            }
            if ("false".equalsIgnoreCase(value)) {
                return false;
            }
            throw new IllegalArgumentException("Cannot convert value \"" + value + "\" to boolean");
        };
    }
}
