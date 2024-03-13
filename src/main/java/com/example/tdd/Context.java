package com.example.tdd;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (componentWithConstructorClass.getDeclaredConstructors().length > 1) {
            throw new MultipleInjectedConstructorException();
        }
        //实现类中既不存在，加了@inject注解的构造器 也不存在默认的空参构造器 指定一个异常报错
        if (Arrays.stream(componentWithConstructorClass.getDeclaredConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).toList().size() == 0 && Arrays.stream(componentWithConstructorClass.getDeclaredConstructors()).filter(c -> c.getParameterCount() == 0).toList().size() == 0) {
            throw new AnnotatedConstructorNotFoundException();
        }
        return Arrays.stream(componentWithConstructorClass.getDeclaredConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).findFirst().orElseGet(() -> {
            try {
                return componentWithConstructorClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
