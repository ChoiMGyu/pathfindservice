/*
 * 클래스 기능 : JWT 토큰 관련 서비스를 제공하는 클래스
 * 최근 수정 일자 : 2024.08.10(토)
 */
package com.pathfind.system.service;

import com.pathfind.system.jwtDto.IssuedTokenCSResponse;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtServiceImpl {

    private final SecretKey secretKey;

    private final Long accessTokenExpirationPeriod;

    private final Long refreshTokenExpirationPeriod;

    public JwtServiceImpl(@Value("${jwt.secretKey}") String secret, @Value("${jwt.access.expiration}") Long accessTokenExpirationPeriod, @Value("${jwt.refresh.expiration}") Long refreshTokenExpirationPeriod) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpirationPeriod = accessTokenExpirationPeriod;
        this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriod;
    }

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USER_ID_CLAIM = "userId";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //private final RedisUtil redisUtil;

    public IssuedTokenCSResponse createToken(String userId) {
        String accessToken = createAccessToken(userId);
        String refreshToken = createRefreshToken(userId);

        //redisUtil.setDataExpire(accessToken, refreshToken, refreshTokenExpirationPeriod);
        return new IssuedTokenCSResponse(accessToken, refreshToken);
    }

    private String createAccessToken(String userId) {
        return Jwts.builder()
                .claim("sub", ACCESS_TOKEN_SUBJECT)
                .claim(USER_ID_CLAIM, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshToken(String userId) {
        return Jwts.builder()
                .claim("sub", REFRESH_TOKEN_SUBJECT)
                .claim(USER_ID_CLAIM, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .signWith(secretKey)
                .compact();
    }

//    public boolean isValidToken(String token) {
//        try {
//            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
//
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            logger.info("isValidToken(): token is not valid!!!");
//            return false;
//        }
//    }

    public String getUserId(String accessToken) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload().get(USER_ID_CLAIM, String.class);
    }

    public String getSub(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("sub", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String reIssueAccessToken(String refreshToken) {
        String userId = getUserId(refreshToken);
        logger.info("reIssueAccessToken userId: " + userId);
        //return isValidToken(refreshToken) ? createAccessToken(userId) : null;
        return createAccessToken(userId);
    }
}