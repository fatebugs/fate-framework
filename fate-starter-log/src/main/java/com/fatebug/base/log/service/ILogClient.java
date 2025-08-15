package com.fatebug.base.log.service;

import com.fatebug.base.core.api.R;
import com.fatebug.base.log.constants.EventConstant;
import com.fatebug.base.log.domain.LogApi;
import com.fatebug.base.log.domain.LogError;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        value = EventConstant.FATE_LOG,
        fallback = LogClientFallback.class
)
public interface ILogClient {

    String API_PREFIX = "/log";

    @PostMapping(API_PREFIX + "/saveLog")
    R<Boolean> saveLog(@RequestBody LogApi log);

    @PostMapping(API_PREFIX + "/saveErrorLog")
    R<Boolean> saveErrorLog(@RequestBody LogError log);

}
