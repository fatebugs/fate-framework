package com.fatebug.base.log.handler;

import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.core.api.R;
import com.fatebug.base.core.exception.AuthException;
import com.fatebug.base.core.exception.FateException;
import com.fatebug.base.core.exception.PermissionException;
import com.fatebug.base.log.publisher.LogErrorPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public R<?> handleFateException(FateException ex) {
        log.error("FateException: ", ex);
        return R.error(ex.getErrorCode(), ex.getErrorMsg());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public R<?> handleAuthException(AuthException ex) {
        log.error("认证异常: ", ex);
        return R.error(ex.getErrorCode(), ex.getErrorMsg());
    }

    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public R<?> handlePermissionException(PermissionException ex) {
        log.error("权限异常: ", ex);
        return R.error(ex.getErrorCode(), ex.getErrorMsg());
    }

    /**
     * spring参数校验异常拦截
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        FieldError fieldError = Objects.requireNonNull(ex.getFieldError());
        log.error("参数校验异常: {}", fieldError.getDefaultMessage());
        //构建返回体
        R<?> body = R.error(Objects.requireNonNull(status).value(), fieldError.getDefaultMessage());
        //返回
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("资源未找到异常: ", ex);
        return new ResponseEntity<>(R.error(status.value(), ex.getMessage()), headers, status);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleThrowable(Throwable ex) {
        log.error("服务器异常: ", ex);

        LogErrorPublisher.publishEvent(ex);

        //生成简略的异常信息
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String[] stackTraceStr = Arrays.stream(stackTrace).map(StackTraceElement :: toString).toArray(String[] :: new);
        StringBuilder message = new StringBuilder(ex.getClass().getName() + ": " + ex.getMessage());
        //匹配到第一个包含com.fatebug的堆栈信息
        for (StackTraceElement element : stackTrace) {
            if (element.toString().contains(SysConstants.BASE_PACKAGES)) {
                message.append("\n").append(element);
                break;
            } else {
                message.append("\n").append(element);
            }
        }

        return R.error(500, message.toString());
    }
}