package moe.fotone.event.common.auth.service;

import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.TwitterUser;
import moe.fotone.event.common.auth.userinfo.TwitterUserInfo;
import moe.fotone.event.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = super.loadUser(userRequest);
        TwitterUserInfo userInfo = new TwitterUserInfo(oAuth2User.getAttributes());

        String id = userInfo.getId();
        log.info(id);
        User user = userRepository.findById(Long.parseLong(id)).get();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole().getKey());

        return new TwitterUser(
                user.getId(),
                user.getRole(),
                List.of(simpleGrantedAuthority),
                oAuth2User.getAttributes()
        );
    }
}