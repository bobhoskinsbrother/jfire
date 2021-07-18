package org.github;

import org.github.converter.TypeConverter;

import java.util.HashMap;
import java.util.Map;

public class JFire {

    public static void fire(Object object, String... args) {
        fire(object, new HashMap<>(), args);
    }

    public static void fire(Object object, Map<Class<?>, TypeConverter> converters, String... args) {
        final InternalFire fire = new InternalFire(converters);
        try {
            fire.fire(object, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
