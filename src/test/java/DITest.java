import com.example.tdd.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DITest {
    interface Component {
    }
    static public class ComponentWithDefaultConstructor implements Component {
        public ComponentWithDefaultConstructor() {
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
}
