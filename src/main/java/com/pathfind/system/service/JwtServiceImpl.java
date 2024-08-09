/*
 * 클래스 기능 : JWT 토큰 관련 서비스를 제공하는 클래스
 * 최근 수정 일자 : 2024.08.08(목)
 */
package com.pathfind.system.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.pathfind.system.jwtDto.IssuedTokenCSResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

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
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .withClaim(USER_ID_CLAIM, userId)
                .sign(Algorithm.HMAC512(secretKey));
    }

    private String createRefreshToken(String userId) {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .withClaim(USER_ID_CLAIM, userId)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public boolean isValidToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            logger.info("isValidToken(): token is not valid!!!");
            return false;
        }
    }

    public String getUserId(String accessToken) {
        return JWT.decode(accessToken).getClaim(USER_ID_CLAIM).toString().replace("\"", "");
    }

    public LocalDateTime getExpirationDate(String token) {
        return JWT.decode(token).getExpiresAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String reIssueAccessToken(String refreshToken) {
        String userId = getUserId(refreshToken);
        logger.info("reIssueAccessToken userId: " + userId);
        return isValidToken(refreshToken) ? createAccessToken(userId) : null;
    }
}
