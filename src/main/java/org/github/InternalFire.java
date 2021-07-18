package org.github;

import org.github.converter.Converter;
import org.github.converter.TypeConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public final class InternalFire {

    private final Converter converter;

    public InternalFire(Map<Class<?>, TypeConverter> additionalConverters) {
        converter = new Converter(additionalConverters);
    }

    public void fire(Object toRun, String... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please pass the public method name that you would like to call");
        }
        final String methodName = args[0];
        final Class<?> target = toRun.getClass();
        final List<Method> publicMethods = collectPublicMethods(target);
        if(publicMethods.isEmpty()) {
            throw new IllegalArgumentException("There are no public methods in the class: "+target.getCanonicalName());
        }
        Map<String, String> parameters = parseParameters(Arrays.asList(args).subList(1, args.length));
        try {
            Method method = findMethod(publicMethods, methodName, parameters);
            Object[] parameterValues = orderAndConvertParameterValues(method.getParameters(), parameters);
            if (method.getReturnType() == Void.TYPE) {
                method.invoke(toRun, parameterValues);
            } else {
                Object object = method.invoke(toRun, parameterValues);
                System.out.println(object.toString());
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(buildMessage(e.getMessage(), publicMethods));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Method> collectPublicMethods(Class<?> target) {
        List<String> objectLevelMethods = Arrays.stream(Object.class.getMethods()).map(Method::getName).collect(Collectors.toList());
        Method[] methods = target.getMethods();
        if (methods.length == 0) {
            throw new IllegalArgumentException("There are no callable public methods on class: " + target.getCanonicalName());
        }
        return Arrays.stream(methods).filter(method -> !objectLevelMethods.contains(method.getName())).collect(Collectors.toList());
    }

    private Object[] orderAndConvertParameterValues(Parameter[] parameters, Map<String, String> parametersValues) {
        LinkedList<Object> reply = new LinkedList<>();
        for (Parameter parameter : parameters) {
            final String parameterName = parameter.getName();
            final String value = parametersValues.get(parameterName);
            final Class<?> type = parameter.getType();
            try {
                Object convertedValue = converter.convert(value, type);
                reply.add(convertedValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage() + " for parameter: \"" + parameterName + "\". Please register converters for non basic objects");
            }
        }
        return reply.toArray(new Object[0]);
    }

    private Map<String, String> parseParameters(List<String> arguments) {
        return arguments.stream().map(argument -> argument.substring(2).split("=")).collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1].trim()));
    }

    private Method findMethod(List<Method> methods, String methodName, Map<String, String> parameters) throws NoSuchMethodException {
        final List<Method> filtered = methods.stream().filter(m -> methodName.equals(m.getName()) && parametersEquals(m.getParameters(), parameters.keySet())).collect(Collectors.toList());
        if (!filtered.isEmpty()) {
            return filtered.get(0);
        }
        throw new NoSuchMethodException(methodName);
    }

    private boolean parametersEquals(Parameter[] parameters, Set<String> parameterNames) {
        final HashSet<Object> toCompare = new HashSet<>();
        final HashSet<Object> passedParameters = new HashSet<>(parameterNames);
        Arrays.stream(parameters).forEach(parameter -> {
            if (!parameter.isNamePresent()) {
                throw new UnsupportedOperationException("""
                        There are no parameter names in the compiled class files. \s
                        You need to add the following to your build.gradle file:
                        tasks.withType(JavaCompile) {
                            options.compilerArgs << '-parameters'
                        }""");
            }
            final String name = parameter.getName();
            toCompare.add(name);
        });
        return toCompare.equals(passedParameters);
    }

    private String buildMessage(String methodName, List<Method> publicMethods) {
        StringBuilder reply = new StringBuilder("Method \"").append(methodName).append("\" is not found").append(". \n")
                .append("Public methods are: \"");
        final Iterator<Method> iterator = publicMethods.iterator();
        while (iterator.hasNext()) {
            reply.append(iterator.next().getName());
            if (iterator.hasNext()) {
                reply.append(", ");
            }
        }
        reply.append("\"");
        return reply.toString();
    }

}
