package moe.fotone.event.api.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.BaseResponse;
import moe.fotone.event.api.auth.response.TokenServiceResponse;
import moe.fotone.event.api.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public BaseResponse<TokenServiceResponse> loginKakao(
            @RequestParam(name = "code") String code,
            HttpServletResponse response) throws JsonProcessingException {
        TokenServiceResponse tokenServiceResponse = authService.twitterLogin(code);

        // tokenCookieManager.addRefreshTokenCookie(response, tokenServiceResponse.getRefreshToken());
        return BaseResponse.OK(tokenServiceResponse);
    }

}
