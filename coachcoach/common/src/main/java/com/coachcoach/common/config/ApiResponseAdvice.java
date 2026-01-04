package com.coachcoach.common.config;

import com.coachcoach.common.dto.ApiResponse;
import com.coachcoach.common.dto.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return !ApiResponse.class.isAssignableFrom(parameterType)
                && !ErrorResponse.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType contentType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof ApiResponse || body instanceof ErrorResponse) {
            return body;
        }

        if (body instanceof String) {
            return ApiResponse.success(body);
        }

        if (body == null) {
            HttpStatus status = extractHttpStatus(response);

            // 204 No Content는 래핑 안 함
            if (status == HttpStatus.NO_CONTENT) {
                return null;
            }

            return ApiResponse.success(null);
        }

        return ApiResponse.success(body);
    }

    private HttpStatus extractHttpStatus(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            HttpStatus resolved = HttpStatus.resolve(
                    servletResponse.getServletResponse().getStatus()
            );
            return resolved != null ? resolved : HttpStatus.OK;
        }
        return HttpStatus.OK;
    }
}