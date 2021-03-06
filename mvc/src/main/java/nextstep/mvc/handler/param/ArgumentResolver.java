package nextstep.mvc.handler.param;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.mvc.exception.UnHandledRequestException;
import nextstep.web.annotation.Controller;
import nextstep.web.annotation.RequestParam;
import nextstep.web.annotation.SessionAttribute;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class ArgumentResolver {

    private ArgumentResolver() {
    }

    public static Object[] resolveRequestParam(Object controller, Method method,
                                               HttpServletRequest request, HttpServletResponse response) {
        if (controller instanceof Controller) {
            return new Object[]{request, response};
        }
        return Arrays.stream(method.getParameters())
                .map(parameter -> getParameter(parameter, request, response))
                .toArray();
    }

    private static Object getParameter(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (parameter.getDeclaredAnnotations().length > 0) {
            return getParameterByAnnotation(parameter, request);
        }

        if (parameter.getType().equals(HttpServletRequest.class)) {
            return request;
        }

        if (parameter.getType().equals(HttpServletResponse.class)) {
            return response;
        }

        throw new UnHandledRequestException("적절하지 않은 인자입니다.");
    }

    private static Object getParameterByAnnotation(Parameter parameter, HttpServletRequest request) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            String requestParamValue = parameter.getAnnotation(RequestParam.class).value();
            return request.getParameter(requestParamValue);
        }

        if (parameter.isAnnotationPresent(SessionAttribute.class)) {
            return request.getSession();
        }

        throw new UnHandledRequestException("적절하지 않은 어노테이션 매핑입니다.");
    }
}
