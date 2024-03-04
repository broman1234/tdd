package com.example.tdd;

import java.util.HashMap;
import java.util.Map;

public class Context {
    Map<Class<?>, Object> instances =new HashMap<>();
    public <Component> void bind(Class<Component> type, Component instance) {
        // TODO document why this method is empty
        instances.put(type,instance);
    }

    public <Component> Component get(Class<?> componentClass) {
        return (Component) instances.get(componentClass);
    }

    public <Component, Implementation extends Component> void bind(Class<Component> componentClass, Class<Implementation> componentWithDefaultConstructorClass) {

    }
}
