package com.fatebug.base.core.exception;

import com.fatebug.base.core.api.RespStatus;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FateException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = -1564047601605339842L;

    /**
     * 错误码
     */
    protected int errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public FateException() {
        super();
    }

    public FateException(RespStatus status) {
        super(String.valueOf(status.getCode()));
        this.errorCode = status.getCode();
        this.errorMsg = status.getMsg();
    }

    public FateException(RespStatus status, Throwable cause) {
        super(String.valueOf(status.getCode()), cause);
        this.errorCode = status.getCode();
        this.errorMsg = status.getMsg();
    }

    public FateException(String errorMsg) {
        super(errorMsg);
        this.errorCode= HttpStatus.BAD_REQUEST.value();
        this.errorMsg = errorMsg;
    }

    public FateException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public FateException(int errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
