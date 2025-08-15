package com.fatebug.base.security.bodyEncode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 请求加密配置类
 * 用于配置RSA加密的私钥、公钥、字符集等参数
 */
@ConfigurationProperties(prefix = "rsa.encrypt")
@Configuration
public class SecretKeyConfig{

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 字符集
     * 默认使用UTF-8编码
     */
    private String charset = "UTF-8";

    /**
     * 是否开启加密
     */
    private boolean open = false;

    /**
     * 是否显示日志
     * 默认为false，表示不显示加密解密日志
     */
    private boolean showLog = false;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }
}
