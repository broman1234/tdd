import com.example.tdd.Context;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DITest {
    interface Component {
    }

    interface Dependency {
    }

    static public class DependencyWithMultipleParameterConstructor implements Dependency{
        String name;
        Integer age;

        @Inject
        public DependencyWithMultipleParameterConstructor(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }

    static public class ComponentWithTransitiveDependencyConstructor implements Component {
        Dependency dependency;

        @Inject
        public ComponentWithTransitiveDependencyConstructor(Dependency dependency) {
            this.dependency = dependency;
        }

        public Dependency getDependency() {
            return dependency;
        }
    }
    static public class ComponentWithDefaultConstructor implements Component {
        public ComponentWithDefaultConstructor() {
        }
    }

    static public class ComponentWithConstructor implements Component {
        String name;
        @Inject
        public ComponentWithConstructor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static public class ComponentWithMultipleParameterConstructor implements Component {
        String name;

        Integer age;

        @Inject
        public ComponentWithMultipleParameterConstructor(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }

    //todo: context should get a constructed component instance
    //todo: get and verify this component instance from context

    @Test
    void should_bind_type_to_a_specific_instance() {
        Context context = new Context();
        Component instance = new Component() {};

        context.bind(Component.class, instance);

        assertSame(instance, context.get(Component.class));
    }


    // todo: context should construct a component instance using no args constructor with Class as input
    // todo: get and this component instance from context is not null
    // todo: verify this component instance from context is an instance of Component
    @Test
    void should_bind_type_to_a_no_args_constructor() {
        Context context = new Context();

        context.bind(Component.class, ComponentWithDefaultConstructor.class);

        Component instance = context.get(Component.class);
        assertNotNull(instance);
        assertTrue(instance instanceof Component);
    }

    @Test
    void should_bind_type_to_a_class_with_inject_constructor() {
        Context context = new Context();

        context.bind(Component.class, ComponentWithConstructor.class);
        context.bind(String.class, "foo");

        ComponentWithConstructor instance = (ComponentWithConstructor) context.get(Component.class);
        assertNotNull(instance);
        assertTrue(instance instanceof Component);
        assertSame(instance.getName(), "foo");
    }

    @Test
    void should_bind_type_to_a_class_with_inject_multiple_parameter_constructor() {
        Context context = new Context();

        context.bind(Component.class, ComponentWithMultipleParameterConstructor.class);
        context.bind(String.class, "foo");
        Integer age = Integer.valueOf(150);
        context.bind(Integer.class, age);

        ComponentWithMultipleParameterConstructor instance = (ComponentWithMultipleParameterConstructor) context.get(Component.class);
        assertNotNull(instance);
        assertTrue(instance instanceof Component);
        assertSame(instance.getName(), "foo");
        assertSame(instance.getAge(), age);
    }

    @Test
    void should_bind_type_to_a_class_with_transitive_dependency() {
        Context context = new Context();

        context.bind(Component.class, ComponentWithTransitiveDependencyConstructor.class);
        context.bind(Dependency.class, DependencyWithMultipleParameterConstructor.class);
        context.bind(String.class, "foo");
        Integer age = Integer.valueOf(150);
        context.bind(Integer.class, age);

        ComponentWithTransitiveDependencyConstructor instance = (ComponentWithTransitiveDependencyConstructor) context.get(Component.class);
        assertSame(((DependencyWithMultipleParameterConstructor) instance.getDependency()).getName(), "foo");
        assertSame(((DependencyWithMultipleParameterConstructor) instance.getDependency()).getAge(), age);
    }
}
