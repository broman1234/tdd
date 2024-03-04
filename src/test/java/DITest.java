import com.example.tdd.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


interface Component {
}

class ComponentWithDefaultConstructor implements Component {
    public ComponentWithDefaultConstructor() {

    }
}
class DITest {

    //todo: Instance



    @Test
    void should_bind_type_to_a_specific_instance() {
        Context context = new Context();
        Component instance = new Component() {};

        context.bind(Component.class, instance);

        assertSame(instance, context.get(Component.class));
    }

    @Test
    void should_bind_type_to_a_no_args_contructor() {
        Context context = new Context();
        ComponentWithDefaultConstructor constructor = new ComponentWithDefaultConstructor();

        context.bind(Component.class, ComponentWithDefaultConstructor.class);
        Component instance = context.get(Component.class);

        assertNotNull(instance);
        assertTrue(instance instanceof ComponentWithDefaultConstructor);
    }
}
