package com.fatebug.base.log.service;

import com.fatebug.base.core.api.R;
import com.fatebug.base.log.domain.LogApi;
import com.fatebug.base.log.domain.LogError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LogClientFallback implements ILogClient {
    @Override
    public R<Boolean> saveLog(LogApi log) {
        return R.error("日志保存失败");
    }

    @Override
    public R<Boolean> saveErrorLog(LogError log) {
        return R.error("日志保存失败");
    }
}
