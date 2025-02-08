package moe.fotone.event.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@AllArgsConstructor
// user 는 DB 에서의 예약어
public class User extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String socialId;

    @Column(nullable = false, length = 50)
    private String username;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(length = 500)
    private String introduce;

    @Column
    private String picture;

    @URL @Column
    private String socialLink;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Enumerated(EnumType.STRING)
    @NotNull
    private SocialType socialType;

    @Column
    @NotNull
    private Boolean activated;

    @Column
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static User createUser(String socialId, String email, String name, String picture) {
        return User.builder()
                .socialType(SocialType.TWITTER)
                .socialId(socialId)
                .email(email)
                .username(name)
                .picture(picture)
                .activated(true)
                .role(Role.GUEST)
                .build();
    }

    public void signupUser(String username, String introduce) {
        this.role = Role.USER;
        this.username = username;
        this.introduce = introduce;
        this.isPublic = true;
    }
}
