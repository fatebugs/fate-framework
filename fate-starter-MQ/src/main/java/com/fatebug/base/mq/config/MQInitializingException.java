package com.fatebug.base.mq.config;

import com.fatebug.base.core.api.RespStatus;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MQInitializingException extends RuntimeException implements Serializable {

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

    public MQInitializingException() {
        super();
    }

    public MQInitializingException(RespStatus status) {
        super(String.valueOf(status.getCode()));
        this.errorCode = status.getCode();
        this.errorMsg = status.getMsg();
    }

    public MQInitializingException(RespStatus status, Throwable cause) {
        super(String.valueOf(status.getCode()), cause);
        this.errorCode = status.getCode();
        this.errorMsg = status.getMsg();
    }

    public MQInitializingException(String errorMsg) {
        super(errorMsg);
        this.errorCode= HttpStatus.BAD_REQUEST.value();
        this.errorMsg = errorMsg;
    }

    public MQInitializingException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public MQInitializingException(int errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
