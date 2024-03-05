package com.example.tdd;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Context {
    Map<Class<?>, Object> instances =new HashMap<>();
    Map<Class<?>, Class<?>> implementations = new HashMap<>();
    public <Component> void bind(Class<Component> type, Component instance) {
        instances.put(type,instance);
    }

    public <Component> Component get(Class<?> componentClass) {
        if (instances.containsKey(componentClass))
            return (Component) instances.get(componentClass);
        try {
            return (Component) implementations.get(componentClass).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public <Component, Implementation extends Component> void bind(Class<Component> componentClass, Class<Implementation> componentWithDefaultConstructorClass) {
        implementations.put(componentClass,componentWithDefaultConstructorClass);
    }
}
