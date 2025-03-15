package moe.fotone.event.api.user;


import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.BaseResponse;
import moe.fotone.event.api.user.response.UserResponse;
import moe.fotone.event.api.user.service.UserService;
import moe.fotone.event.common.auth.TwitterUser;
import moe.fotone.event.common.auth.userinfo.OAuth2UserInfo;
import moe.fotone.event.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public BaseResponse<UserResponse> getMyInfo(@AuthenticationPrincipal TwitterUser user) {

        User me = userService.findUserById(user.getUserId());

        return BaseResponse.OK(UserResponse.of(me));
    }

    @GetMapping("/info/{userId}")
    public BaseResponse<UserResponse> getUserInfo(@PathVariable Long userId) {
        User user = userService.findUserById(userId);

        return BaseResponse.OK(UserResponse.of(user));
    }

    @PatchMapping("/me")
    public BaseResponse<UserResponse> updateMyInfo(@AuthenticationPrincipal TwitterUser user) {

        User me = userService.findUserById(user.getUserId());

        return BaseResponse.OK(UserResponse.of(me));
    }
}
