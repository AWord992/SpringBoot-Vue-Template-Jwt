package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;

    public boolean invalidateJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if (token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean deleteToken(String uuid, Date time){
        if (this.isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "1", expire, TimeUnit.MICROSECONDS);
        return true;
    }

    private boolean isInvalidToken(String uuid){
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    public DecodedJWT resolveJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
//            验证是否在黑名单
            if(this.isInvalidToken(jwt.getId())) return null;
            Date expireTime = jwt.getExpiresAt();
            return new Date().after(expireTime) ? null : jwt;
        } catch (JWTVerificationException e) {
            // 过期或验证失败
            return null;
        }
    }

    public String createJwt(UserDetails details, int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expireTime = this.expireTime();
        System.out.println(details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expireTime)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }

    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        System.out.println("claims: name=" + claims.get("name").asString());
        System.out.println("claims: authorities=");
        Arrays.stream(claims.get("authorities").asArray(String.class)).forEach(System.out::println);
        return User
                .withUsername(claims.get("name").asString())
                .password("****")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public int toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    private String convertToken(String headerToken) {
        if (headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }
}
