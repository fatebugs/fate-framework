package com.fatebug.base.utils.code.encodeUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 封装各种格式的编码解码工具类.
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 */
public class Base64Utils {

    private static final String ENCODING = "UTF-8";

    private static final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * Base64编码.
     */
    public static String encodeBase64(byte[] input) {
        return encoder.encodeToString(input);
    }

    /**
     * Base64编码.指定字符集为UTF-8
     */
    public static String encodeBase64(String input) throws UnsupportedEncodingException {
        try {
            return encoder.encodeToString(input.getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    /**
     * Base64解码.
     */
    public static byte[] decodeBase64(String input) {
        return encoder.encode(input.getBytes());
    }

    /**
     * Base64解码.指定字符集为UTF-8
     */
    public static String decodeBase64String(String input) throws UnsupportedEncodingException {
        return new String(encoder.encode(input.getBytes()), ENCODING);
    }


}
