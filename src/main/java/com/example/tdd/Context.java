package com.example.tdd;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class Context {
    Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Component> void bind(Class<Component> type, Component instance) {
        providers.put(type, (Provider<Component>) () -> instance);
    }

    //return optional empty if the componentClass is not found instead of returning null
    public <Component> Optional<Component> get(Class<Component> componentClass) {
        return (Optional<Component>) Optional.ofNullable(providers.get(componentClass)).map(Provider::get);
    }

    public <Component, Implementation extends Component> void bind(Class<Component> componentClass, Class<Implementation> componentWithConstructorClass) {
        providers.put(componentClass, new ProviderImpl<Implementation>(componentWithConstructorClass, this));
    }

    //create a provider implementation class to implement Provider

    public static class ProviderImpl<T> implements Provider<T> {
        private Class<T> componentWithConstructorClass;

        private Context context;

        public ProviderImpl(Class<T> componentWithConstructorClass, Context context) {
            this.componentWithConstructorClass = componentWithConstructorClass;
            this.context = context;
        }

        @Override
        public T get() {
            Constructor<?> constructor = getInjectConstructor(componentWithConstructorClass);
            Object[] dependencies = Arrays.stream(constructor.getParameterTypes()).map(parameterType -> context.get(parameterType).orElseThrow(() -> new DependencyNotFoundException(parameterType, componentWithConstructorClass))).toArray(Object[]::new);

            try {
                return (T) constructor.newInstance(dependencies);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
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
