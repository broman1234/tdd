package com.example.tdd;

import jakarta.inject.Provider;

import java.lang.reflect.InvocationTargetException;
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


    public <Component, Implementation extends Component> void bind(Class<Component> componentClass, Class<Implementation> componentWithDefaultConstructorClass) {
        providers.put(componentClass, (Provider<Implementation>) () -> {
            try {
                return componentWithDefaultConstructorClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
