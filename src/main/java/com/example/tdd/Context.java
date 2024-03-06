package com.example.tdd;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            Constructor<?> constructor = getInjectConstructor(componentWithConstructorClass);
            Object[] dependencies = Arrays.stream(constructor.getParameterTypes()).map(this::get).toArray(Object[]::new);

            try {
                return (Implementation) constructor.newInstance(dependencies);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static <Component, Implementation extends Component> Constructor<?> getInjectConstructor(Class<Implementation> componentWithConstructorClass) {
        return Arrays.stream(componentWithConstructorClass.getDeclaredConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).findFirst().orElseGet(() -> {
            try {
                return componentWithConstructorClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
