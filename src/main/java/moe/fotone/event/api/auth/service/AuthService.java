package moe.fotone.event.api.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.auth.impl.TwitterLogin;
import moe.fotone.event.api.auth.response.TokenServiceResponse;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.jwt.service.JwtService;
import moe.fotone.event.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TwitterLogin twitterLogin;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public TokenServiceResponse twitterLogin(String code) throws JsonProcessingException {
        String token = twitterLogin.getToken(code);
        User user = twitterLogin.getUser(token);

        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId(), String.valueOf(user.getRole()));
        String refreshToken = jwtService.createRefreshToken();

        TokenServiceResponse tokenServiceResponse = TokenServiceResponse.of(accessToken, refreshToken);
        saveRefreshToken(user, tokenServiceResponse);

        return tokenServiceResponse;
    }

    private void saveRefreshToken(User user, TokenServiceResponse response) {
        userRepository.findById(user.getId())
                .ifPresentOrElse(
                        curUser -> curUser.updateRefreshToken(response.getRefreshToken()),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }
}
