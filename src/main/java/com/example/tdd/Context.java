package com.example.tdd;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Context {
    Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Component> void bind(Class<Component> type, Component instance) {
        providers.put(type, (Provider<Component>) () -> instance);
    }

    public <Component> Component get(Class<?> componentClass) {
        return (Component) providers.get(componentClass).get();

    }


    public <Component, Implementation extends Component> void bind(Class<Component> componentClass, Class<Implementation> componentWithConstructorClass) {
        providers.put(componentClass, (Provider<Implementation>) () -> {
            try {
                Stream<Constructor<?>> constructorStream = Arrays.stream(componentWithConstructorClass.getDeclaredConstructors()).filter(constructor -> constructor.isAnnotationPresent(Inject.class));

                Optional<Constructor<?>> constructorOptional = constructorStream.findFirst();
                if (constructorOptional.isPresent()) {
                    Constructor<?> constructor = constructorOptional.get();
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    List<?> dependencies = Arrays.stream(parameterTypes).map(parameterType -> providers.get(parameterType).get()).collect(Collectors.toList());
                    return (Implementation) constructor.newInstance(dependencies.toArray());
                }
                return componentWithConstructorClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
