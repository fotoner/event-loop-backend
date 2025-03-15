package moe.fotone.event.common.auth.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.TwitterUser;
import moe.fotone.event.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.name}")
    private String refreshTokenCookieName;

    private static final int DEFAULT_EXPIRATION = 24 * 60 * 60;
    private static final String COOKIE_PATH = "/";

    private static final String ID_CLAIM = "id";
    private static final String ROLE_CLAIM = "role";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;

    public String createAccessToken(Long userId, String role) {
        Date now = new Date();

        return JWT.create()
                .withSubject(accessHeader)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(ID_CLAIM, userId)
                .withClaim(ROLE_CLAIM, role)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(refreshTokenCookieName)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public Authentication getAuthentication(String accessToken) {
        Map<String, Claim> claims = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(accessToken)
                .getClaims();

        List<String> authorities = Arrays.asList(claims.get(ROLE_CLAIM).toString().split(","));

        List<? extends GrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        log.info("id = {}, role = {}", claims.get(ID_CLAIM).asLong(), claims.get(ROLE_CLAIM).asString());
        TwitterUser user = new TwitterUser(
                claims.get(ID_CLAIM).asLong(),
                Role.fromString(claims.get(ROLE_CLAIM).asString()),
                simpleGrantedAuthorities,
                Map.of()
        );

        return new UsernamePasswordAuthenticationToken(user, accessToken, simpleGrantedAuthorities);
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addRefreshTokenCookie(response, refreshToken, DEFAULT_EXPIRATION);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .path(COOKIE_PATH)
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, "")
                .path(COOKIE_PATH)
                .maxAge(0)  // 즉시 만료
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .map(Arrays::asList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny();
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
