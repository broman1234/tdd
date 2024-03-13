package com.example.tdd;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        Constructor<?>[] declaredConstructors = componentWithConstructorClass.getDeclaredConstructors();
        List<Constructor<?>> injectedConstructor = Arrays.stream(declaredConstructors).filter(c -> c.isAnnotationPresent(Inject.class)).toList();
        if (injectedConstructor.size() > 1) {
            throw new MultipleInjectedConstructorException();
        }
        if (injectedConstructor.size() == 1) {
            return injectedConstructor.get(0);
        }
        // 当找不到@Injected注解的构造器时，去寻找无参构造器
        //   如果存在无参构造器，那么返回这个无参构造器
        //   如果不存在无参构造器，抛出异常
        return Arrays.stream(declaredConstructors).filter(c -> c.getParameterCount() == 0).findFirst().orElseThrow(() -> {throw new AnnotatedConstructorNotFoundException();});
    }
}
