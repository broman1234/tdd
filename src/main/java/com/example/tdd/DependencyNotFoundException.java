package com.example.tdd;

public class DependencyNotFoundException extends RuntimeException {
    Class<?> dependency;

    Class<?> component;

    public DependencyNotFoundException(Class<?> dependency, Class<?> component) {
        this.dependency = dependency;
        this.component = component;
    }

    public Class<?> getDependency() {
        return dependency;
    }

    public Class<?> getComponent() {
        return component;
    }
}
