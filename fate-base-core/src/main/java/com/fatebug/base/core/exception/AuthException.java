package com.fatebug.base.core.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AuthException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = -1564047601605339842L;

    /**
     * 错误码
     */
    protected int errorCode = HttpStatus.UNAUTHORIZED.value();
    /**
     * 错误信息
     */
    protected String errorMsg;

    public AuthException() {
        super();
    }

    public AuthException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }


    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
