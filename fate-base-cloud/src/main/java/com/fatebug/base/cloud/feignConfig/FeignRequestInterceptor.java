package com.fatebug.base.cloud.feignConfig;

import cn.hutool.core.util.ObjectUtil;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.utils.ServletUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * feign 请求拦截器
 *
 * @author fatebug
 */
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (ObjectUtil.isNotNull(httpServletRequest)) {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            if (!headers.isEmpty()) {
                headers.forEach(requestTemplate :: header);
            }
            requestTemplate.removeHeader("content-length");
            requestTemplate.removeHeader(SysConstants.FROM_SOURCE);
            requestTemplate.header(SysConstants.FROM_SOURCE, SysConstants.INNER);
        }
    }
}
