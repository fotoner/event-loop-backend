package moe.fotone.event.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.fotone.event.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class TwitterUser implements OAuth2User {

    private final Long userId;
    private final Role role;
    private final List<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    @Override
    public String getName() {
        return userId.toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
