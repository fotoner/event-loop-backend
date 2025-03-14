package moe.fotone.event.api.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.BaseResponse;
import moe.fotone.event.api.auth.request.TwitterLoginRequest;
import moe.fotone.event.api.auth.response.TokenServiceResponse;
import moe.fotone.event.api.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("/login")
    public BaseResponse<TokenServiceResponse> loginTwitter(
            @RequestBody TwitterLoginRequest twitterLoginRequest) throws JsonProcessingException {
        log.info(twitterLoginRequest.getCode());
        TokenServiceResponse tokenServiceResponse = authService.twitterLogin(twitterLoginRequest.getCode());

        return BaseResponse.OK(tokenServiceResponse);
    }

}
