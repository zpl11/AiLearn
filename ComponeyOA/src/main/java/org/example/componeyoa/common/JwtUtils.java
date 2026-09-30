package org.example.componeyoa.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 密钥：在生产环境中建议配置在 application.yml 中，且长度不得少于 32 字符（256 位）
    private static final String SECRET_STRING = "CompanyOASystemSecretKeyForJWTAuth2026SecureRandomBits";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // Token 有效期：这里设置为 24 小时（单位：毫秒）
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    /**
     * 根据用户信息生成 Token
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @return 加密签名后的 JWT 字符串
     */
    public static String createToken(Long userId, String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userName", userName);

        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + EXPIRE_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token 并获取载荷中的 Claims
     * 签名失效或过期时，底层会直接抛出 JwtException 异常
     *
     * @param token 前端传过来的 JWT 字符串
     * @return 包含用户信息的 Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中快速提取用户 ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return Long.valueOf(userIdObj.toString());
    }

    /**
     * 从 Token 中快速提取用户名
     */
    public static String getUserName(String token) {
        Claims claims = parseToken(token);
        return claims.get("userName", String.class);
    }
}
