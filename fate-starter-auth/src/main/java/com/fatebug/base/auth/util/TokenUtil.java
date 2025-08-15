package com.fatebug.base.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fatebug.base.core.constants.RedisConstant;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.utils.DateUtils;
import com.fatebug.base.utils.IdWorker;
import com.fatebug.base.utils.SpringUtils;
import com.fatebug.base.utils.StringUtils;
import com.fatebug.base.redis.util.RedisUtil;
import com.fatebug.base.auth.user.TokenInfo;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token工具类
 *
 * @author fatebug
 */
@Slf4j
public class TokenUtil {

    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    public final static Long MILLIS_MINUTE_TEN = SysConstants.REFRESH_TIME * MILLIS_MINUTE;
    private final static long expireTime = SysConstants.EXPIRATION;

    // 14 days
    private final static long refreshExpireTime = 7;
    private static final String secret = "fate";

    private static final String loginUserKey = RedisConstant.USER_REDIS_KEY + SysConstants.LOGIN_USER + SysConstants.COLON;
    private static final String refreshTokenKey = RedisConstant.USER_REDIS_KEY + SysConstants.REFRESH_TOKEN + SysConstants.COLON;
    private static final String accessTokenKey = RedisConstant.USER_REDIS_KEY + SysConstants.ACCESS_TOKEN + SysConstants.COLON;


    private static RedisUtil redisUtil;

    public static RedisUtil getRedisUtil() {
        if (redisUtil == null) {
            redisUtil = SpringUtils.getBean(RedisUtil.class);
        }
        return redisUtil;
    }

    /**
     * 生成token并将用户信息存入Redis中
     */
    public static <T> void createToken(TokenInfo<T> tokenInfo) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(SysConstants.TOKEN_USERID, tokenInfo.getUserId());
        claims.put(SysConstants.TOKEN_USERNAME, tokenInfo.getUserName());
        claims.put(SysConstants.TOKEN_IP, tokenInfo.getLoginIp());

        String refreshToken = generateToken(claims, SysConstants.REFRESH_TOKEN, -1);
        String accessToken = generateToken(claims, SysConstants.ACCESS_TOKEN, expireTime);
        tokenInfo.setAccessToken(accessToken);
        tokenInfo.setRefreshToken(refreshToken);

        //用户信息存入Redis，且不会过期
        getRedisUtil().setCacheObject(
                loginUserKey + tokenInfo.getUserId(),
                tokenInfo
        );
        //存入RefreshToken，用于刷新AccessToken
        getRedisUtil().setCacheObject(
                refreshTokenKey + tokenInfo.getUserId(),
                refreshToken, refreshExpireTime, TimeUnit.DAYS
        );
        getRedisUtil().setCacheObject(
                accessTokenKey + tokenInfo.getUserId(),
                accessToken, expireTime, TimeUnit.MINUTES
        );
    }

    /**
     * 根据RefreshToken刷新AccessToken
     */
    public static <T> void refreshToken(TokenInfo<T> tokenInfo) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(SysConstants.TOKEN_USERID, tokenInfo.getUserId());
        claims.put(SysConstants.TOKEN_USERNAME, tokenInfo.getUserName());
        claims.put(SysConstants.TOKEN_IP, tokenInfo.getLoginIp());

        String accessToken = generateToken(claims, SysConstants.ACCESS_TOKEN, expireTime);
        tokenInfo.setAccessToken(accessToken);
        //用户信息存入Redis，且不会过期
        getRedisUtil().setCacheObject(
                loginUserKey + tokenInfo.getUserId(),
                tokenInfo
        );
        //存入AccessToken
        getRedisUtil().setCacheObject(
                accessTokenKey + tokenInfo.getUserId(),
                accessToken, expireTime, TimeUnit.MINUTES
        );
    }

    /**
     * 根据负责生成JWT的token
     */
    public static String generateToken(Map<String, Object> claims, String subject, long expireTime) {
        Date currentDate = DateUtils.generateCurrentDate();
        JwtBuilder builder = Jwts.builder();
        //如果过期时间小于等于0，则默认设置为永不过期
        if (expireTime > 0) {
            //设置token过期时间
            builder.setExpiration(DateUtils.longToDate(currentDate.getTime() + expireTime * 1000));
        }

        return builder
                .setClaims(claims)
                //设置tokenid
                .setId(IdWorker.nextIdToString())
                //区分AccessToken与RefreshToken
                .setSubject(subject)
                //设置token作者
                .setIssuer(secret)
                //设置token生成时间
                .setIssuedAt(currentDate)
                //设置加密算法与秘钥
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    /**
     * 从token中获取用户信息 传入全类名 传参示例： jwtTokenUtil.getUserDetailsFromToken(token,
     * Class.forName("com.klns.demotest.domain.SysUser"));
     */
    @SuppressWarnings("unchecked")
    public static <T> T getUserDetailsFromToken(String token, String key, Class<?> clazz) {
        T value;
        try {
            if (isTokenExpired(token)) {
                return null;
            }
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return null;
            }
            Object claimValue = claims.get(key);
            String claimValueAsString;

            if (claimValue instanceof String) {
                claimValueAsString = (String) claimValue;
            } else {
                claimValueAsString = String.valueOf(claimValue);
            }

            value = (T) new ObjectMapper().readValue(claimValueAsString, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            value = null;
        }
        return value;
    }

    /**
     * 判断token是否已经过期 true:过期 false:未过期
     */
    public static boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        if (expiredDate == null) {
            //没有过期时间，则不进行处理
            return false;
        } else {
            return expiredDate.before(new Date());
        }
    }

    /**
     * 从token中获取过期时间
     */
    public static Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 从token中获取JWT中的负载数据
     */
    public static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("token已过期");
        } catch (Exception e) {
            log.error("JWT格式验证失败:", e);
            log.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }

    /**
     * 获取储存在token中的userId
     */
    public static String getUserId(String token) {
        return getUserDetailsFromToken(token, SysConstants.TOKEN_USERID, String.class);
    }

    /**
     * 注销登录
     */
    public static void delToken(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userId = getUserId(token);
            getRedisUtil().remove(loginUserKey + userId);
        }
    }

    /**
     * 获取用户信息
     */
    public static <T> TokenInfo<T> getTokenInfo(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userId = getUserId(token);
            if (userId == null) {
                return null;
            }

            TokenInfo<T> tokenInfo = getRedisUtil().getCacheObject(loginUserKey + userId);
            if (
                    !token.equals(tokenInfo.getAccessToken())
                            && !token.equals(tokenInfo.getRefreshToken())
            ) {
                return null;
            }
            return tokenInfo;
        }
        return null;
    }
}
