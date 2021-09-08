package nextstep.mvc.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nextstep.mvc.DispatcherServlet;
import nextstep.mvc.adaptor.ControllerAdapter;
import nextstep.mvc.adaptor.HandlerAdapter;
import nextstep.mvc.adaptor.HandlerAdapters;
import nextstep.mvc.adaptor.HandlerExecutionAdapter;
import nextstep.mvc.handler.exception.ExceptionHandlerExecutor;
import nextstep.mvc.handler.tobe.AnnotationHandlerMapping;
import nextstep.mvc.handler.tobe.HandlerMapping;
import nextstep.mvc.handler.tobe.HandlerMappings;
import nextstep.mvc.view.resolver.ViewResolver;
import nextstep.mvc.view.resolver.ViewResolverImpl;

public class Assembler {

    private final BeanContainer container;

    private final String basePath;

    public Assembler(String basePath) {
        this.basePath = basePath;

        Map<Class<?>, Object> beans = new ComponentScanner().scan(basePath);
        this.container = new BeanContainer(beans);

        this.container.addBean(HandlerMappings.class, handlerMappings());
        this.container.addBean(HandlerAdapters.class, handlerAdaptors());
        this.container.addBean(ExceptionHandlerExecutor.class, new ExceptionHandlerExecutor(basePath));
        this.container.addBean(ViewResolver.class, new ViewResolverImpl());
        this.container.addBean(DispatcherServlet.class, dispatcherServlet());
    }

    public DispatcherServlet dispatcherServlet() {
        HandlerMappings handlerMappings = (HandlerMappings) container.getBeanByType(HandlerMappings.class);
        HandlerAdapters handlerAdaptors = (HandlerAdapters) container.getBeanByType(HandlerAdapters.class);
        ExceptionHandlerExecutor exceptionHandlerExecutor = (ExceptionHandlerExecutor) container.getBeanByType(ExceptionHandlerExecutor.class);
        ViewResolver viewResolver = (ViewResolver) container.getBeanByType(ViewResolver.class);

        final DispatcherServlet dispatcherServlet
                = new DispatcherServlet(handlerMappings, handlerAdaptors, viewResolver, exceptionHandlerExecutor);

        return dispatcherServlet;
    }

    private HandlerAdapters handlerAdaptors() {
        List<HandlerAdapter> handlerAdapters = Arrays.asList(
                new HandlerExecutionAdapter(), new ControllerAdapter()
        );
        return new HandlerAdapters(handlerAdapters);
    }

    private HandlerMappings handlerMappings(){
        AnnotationHandlerMapping annotationHandlerMapping = new AnnotationHandlerMapping(basePath);
        this.container.addBean(AnnotationHandlerMapping.class, annotationHandlerMapping);

        HandlerMappings handlerMappings = new HandlerMappings();
        handlerMappings.add(annotationHandlerMapping);

        return handlerMappings;
    }

    public Object getBeanByType(Class<?> type) {
        return container.getBeanByType(type);
    }
}
