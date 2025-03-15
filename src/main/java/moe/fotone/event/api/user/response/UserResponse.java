package moe.fotone.event.api.user.response;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import moe.fotone.event.domain.User;

@Getter
@Builder
public class UserResponse {
    private Long userId;

    private String username;

    @Email
    private String email;

    private boolean isPublic;

    private String introduce;

    private String picture;

    private String socialLink;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .isPublic(user.isPublic())
                .introduce(user.getIntroduce())
                .picture(user.getPicture())
                .socialLink(user.getSocialLink())
                .build();
    }
}