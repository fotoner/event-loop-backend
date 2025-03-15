package moe.fotone.event.common.auth.jwt.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.jwt.service.JwtService;
import moe.fotone.event.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        log.info("HttpMethod = {}, URI = {}", request.getMethod(), request.getRequestURI());
        String token = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if(isRequestPassURI(request)) {
            Objects.requireNonNull(filterChain).doFilter(request, response);
            return;
        }

        if(token == null) {
            setErrorResponse(response);
            return;
        }

        Authentication authentication = jwtService.getAuthentication(token);

        if (authentication == null) {
            log.error("JWT 인증 정보가 NULL입니다. SecurityContext에 설정하지 않습니다.");
            setErrorResponse(response);
            return;
        }
        log.info("SecurityContext에 저장된 Authentication: {}", authentication.getPrincipal());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Objects.requireNonNull(filterChain).doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response) throws IOException {
        Objects.requireNonNull(response)
                .setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "유효하지 않은 토큰입니다."
        )));
    }

    private static final Pattern USER_INFO_PATTERN = Pattern.compile("^/user/info/\\d+$");

    private static boolean isRequestPassURI(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.equals("/")) {
            return true;
        }

        if (uri.startsWith("/auth")) {
            return true;
        }

        if (uri.startsWith("/playlist/create")) {
            return false;
        }

        if (uri.startsWith("/playlist")) {
            return true;
        }

        if (uri.startsWith("/swagger-ui")) {
            return true;
        }

        if (uri.startsWith("/api-docs")) {
            return true;
        }

        if (uri.startsWith("/upload")) {
            return true;
        }

        if (uri.startsWith("/user/me")) {
            // "/user/info/{id}" 패턴에 맞으면 false 반환
            return false;
        }

        if (uri.startsWith("/user")) {
            return true;
        }

        return false;
    }
}