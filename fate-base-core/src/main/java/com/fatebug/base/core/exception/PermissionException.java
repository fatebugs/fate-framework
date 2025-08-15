package com.fatebug.base.core.exception;

import com.fatebug.base.core.api.RespStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PermissionException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = -1564047601605339842L;

    /**
     * 错误码
     */
    protected int errorCode = RespStatus.FORBIDDEN.getCode();
    /**
     * 错误信息
     */
    protected String errorMsg;

    public PermissionException() {
        super();
    }

    public PermissionException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }


    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
