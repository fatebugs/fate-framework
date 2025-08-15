package com.fatebug.base.utils.code.encodeUtils;

import cn.hutool.core.util.HexUtil;
import com.fatebug.base.core.exception.FateException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;

/**
 * 封装各种格式的编码解码工具类. 1.Commons-Codec的 hex/base64 编码 2.自制的base62 编码 3.Commons-Lang的xml/html escape 4.JDK提供的URLEncoder
 */
public class EncodeUtils {

    private static final String ENCODING = "UTF-8";

    private static final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * Hex编码.
     */
    public static String encodeHex(String input) {
        return new String(HexUtil.encodeHex(input.getBytes()));
    }

    /**
     * Hex解码.
     */
    public static String decodeHex(String input) {
        return Arrays.toString(HexUtil.decodeHex(input.toCharArray()));
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String encodeUrl(String part) throws UnsupportedEncodingException {
        try {
            return URLEncoder.encode(part, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String decodeUrl(String part) {
        try {
            return URLDecoder.decode(part, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new FateException("URL解码失败");
        }
    }

}
