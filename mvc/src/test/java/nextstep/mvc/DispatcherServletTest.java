package nextstep.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.mvc.adaptor.HandlerAdapters;
import nextstep.mvc.exception.UnHandledRequestException;
import nextstep.mvc.handler.exception.ExceptionHandlerExecutor;
import nextstep.mvc.handler.mapping.HandlerExecution;
import nextstep.mvc.handler.mapping.HandlerMappings;
import nextstep.mvc.view.JsonModelMapper;
import nextstep.mvc.view.ModelAndView;
import nextstep.mvc.view.ViewName;
import nextstep.mvc.view.resolver.JsonViewResolver;
import nextstep.mvc.view.resolver.JspViewResolver;
import nextstep.mvc.view.resolver.ViewResolvers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class DispatcherServletTest {

    private final HttpServletRequest httpRequest = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse httpResponse = Mockito.mock(HttpServletResponse.class);

    private final HandlerMappings handlerMappings = Mockito.mock(HandlerMappings.class);
    private final HandlerAdapters handlerAdapters = Mockito.mock(HandlerAdapters.class);
    private final ViewResolvers viewResolvers = Mockito.mock(ViewResolvers.class);
    private final ExceptionHandlerExecutor exceptionHandlerExecutor = Mockito.mock(ExceptionHandlerExecutor.class);

    @DisplayName("ExceptionHandler로 정의한 방식으로 예외를 처리한다.")
    @Test
    void handleNotFoundException() throws Throwable {
        ExceptionHandlerExecutor exceptionHandlerExecutor = new ExceptionHandlerExecutor("samples");
        DispatcherServlet dispatcherServlet = new DispatcherServlet(handlerMappings, handlerAdapters, viewResolvers, exceptionHandlerExecutor);

        Mockito.when(handlerMappings.getHandler(any())).thenThrow(new UnHandledRequestException());

        ModelAndView modelAndView = dispatcherServlet.processRequest(httpRequest, httpResponse);
        assertThat(modelAndView.getViewName()).isEqualTo(ViewName.of("404.html"));
    }

    @DisplayName("JsonView를 반환하는 핸들러의 응답을 확인한다")
    @Test
    void testJsonViewResolver() throws Throwable {
        JspViewResolver jspViewResolver = new JspViewResolver();
        JsonViewResolver jsonViewResolver = new JsonViewResolver();
        ViewResolvers viewResolvers = new ViewResolvers(Arrays.asList(jspViewResolver, jsonViewResolver));

        DispatcherServlet dispatcherServlet = new DispatcherServlet(handlerMappings, handlerAdapters, viewResolvers, exceptionHandlerExecutor);

        HandlerExecution mockHandler = Mockito.mock(HandlerExecution.class);
        Mockito.when(handlerMappings.getHandler(any())).thenReturn(mockHandler);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("key", "value");
        Mockito.when(handlerAdapters.service(httpRequest, httpResponse, mockHandler)).thenReturn(modelAndView);

        PrintWriter mockPrintWriter = Mockito.mock(PrintWriter.class);
        Mockito.when(httpResponse.getWriter()).thenReturn(mockPrintWriter);

        dispatcherServlet.service(httpRequest, httpResponse);
        Mockito.verify(mockPrintWriter).write(JsonModelMapper.parse(modelAndView.getModel()));
    }
}
