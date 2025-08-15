package com.fatebug.base.core.request;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.fatebug.base.core.api.R;

/**
 * 根据返回的 R 对象设置 HTTP 状态码的响应体增强器。
 */
@ConditionalOnClass(ResponseBodyAdvice.class)
@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<R<?>> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getParameterType().isAssignableFrom(R.class);
    }

    @Override
    public R<?> beforeBodyWrite(R<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null) {
            response.setStatusCode(HttpStatus.valueOf(body.getCode()));
        }
        return body;
    }
}