import com.example.tdd.AnnotatedConstructorNotFoundException;
import com.example.tdd.Context;
import com.example.tdd.MultipleInjectedConstructorException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DITest {
    interface Component {
    }

    interface Dependency {
    }

    static public class ComponentWithMultipleAnnotatedConstructor implements Component {
        String name;
        Integer age;

        @Inject
        public ComponentWithMultipleAnnotatedConstructor(String name) {
            this.name = name;
        }

        @Inject
        public ComponentWithMultipleAnnotatedConstructor(Integer age) {
            this.age = age;
        }
    }

    static public class ComponentWithNoAnnotatedConstruction implements Component {
        String name;

        public ComponentWithNoAnnotatedConstruction(String name) {
            this.name = name;
        }
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

    Context context;
    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    void should_bind_type_to_a_specific_instance() {
        Component instance = new Component() {};

        context.bind(Component.class, instance);

        assertSame(instance, context.get(Component.class).get());
    }


    // todo: context should construct a component instance using no args constructor with Class as input
    // todo: get and this component instance from context is not null
    // todo: verify this component instance from context is an instance of Component
    @Test
    void should_bind_type_to_a_no_args_constructor() {

        context.bind(Component.class, ComponentWithDefaultConstructor.class);

        Component instance = context.get(Component.class).get();
        assertNotNull(instance);
        assertTrue(instance instanceof Component);
    }

    @Test
    void should_bind_type_to_a_class_with_inject_constructor() {

        context.bind(Component.class, ComponentWithConstructor.class);
        context.bind(String.class, "foo");

        ComponentWithConstructor instance = (ComponentWithConstructor) context.get(Component.class).get();
        assertNotNull(instance);
        assertTrue(instance instanceof Component);
        assertSame(instance.getName(), "foo");
    }

    @Test
    void should_bind_type_to_a_class_with_inject_multiple_parameter_constructor() {

        context.bind(Component.class, ComponentWithMultipleParameterConstructor.class);
        context.bind(String.class, "foo");
        Integer age = Integer.valueOf(150);
        context.bind(Integer.class, age);

        ComponentWithMultipleParameterConstructor instance = (ComponentWithMultipleParameterConstructor) context.get(Component.class).get();
        assertNotNull(instance);
        assertTrue(instance instanceof Component);
        assertSame(instance.getName(), "foo");
        assertSame(instance.getAge(), age);
    }

    @Test
    void should_bind_type_to_a_class_with_transitive_dependency() {

        context.bind(Component.class, ComponentWithTransitiveDependencyConstructor.class);
        context.bind(Dependency.class, DependencyWithMultipleParameterConstructor.class);
        context.bind(String.class, "foo");
        Integer age = Integer.valueOf(150);
        context.bind(Integer.class, age);

        ComponentWithTransitiveDependencyConstructor instance = (ComponentWithTransitiveDependencyConstructor) context.get(Component.class).get();
        assertSame(((DependencyWithMultipleParameterConstructor) instance.getDependency()).getName(), "foo");
        assertSame(((DependencyWithMultipleParameterConstructor) instance.getDependency()).getAge(), age);
    }

    //1. 实现类中存在多个家了@Inject 注解的constructor的时候需要指定一个人自定的exception
    //2. 实现类中既不存在，加了@inject注解的构造器 也不存在默认的空参构造器 指定一个异常报错
    //3. 如果在实现类寻找Dependency的过程中遇到了 从providers中找不到的情况 需要报Dependency找不到的自定义异常

    @Test
    void should_throw_exception_when_binding_type_to_a_class_given_multiple_annotated_constructor() {
        context.bind(Component.class, ComponentWithMultipleAnnotatedConstructor.class);
        context.bind(String.class, "foo");
        context.bind(Integer.class, 150);

        assertThrows(MultipleInjectedConstructorException.class, () -> context.get(Component.class));
    }

    @Test
    void should_throw_exception_when_binding_type_to_a_class_given_no_annotated_constructor_can_be_found() {
        context.bind(Component.class, ComponentWithNoAnnotatedConstruction.class);
        context.bind(String.class, "foo");

        assertThrows(AnnotatedConstructorNotFoundException.class, () -> context.get(Component.class));
    }
}
