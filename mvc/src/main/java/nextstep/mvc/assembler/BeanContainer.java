package nextstep.mvc.assembler;

import java.util.Map;

public class BeanContainer {

    private final Map<Class<?>, Object> container;

    public BeanContainer(Map<Class<?>, Object> container) {
        this.container = container;
    }

    public Object getBeanByType(Class<?> type) {
        return container.get(type);
    }

    public void addBean(Class<?> type, Object bean) {
        container.put(type, bean);
    }
}
