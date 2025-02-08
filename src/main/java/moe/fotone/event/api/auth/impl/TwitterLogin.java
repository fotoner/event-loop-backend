package moe.fotone.event.api.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.userinfo.TwitterUserInfo;
import moe.fotone.event.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TwitterLogin {
    public static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.security.oauth2.client.registration.twitter.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.twitter.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.twitter.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.twitter.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.twitter.user-info-uri}")
    private String userInfoUri;

    public String getToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code_verifier", "challenge");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                tokenRequest,
                String.class
        );

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    @Transactional
    public User getUser(String token) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<Void> profileRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.POST,
                    profileRequest,
                    String.class
            );

            String responseBody = response.getBody();
            Map<String, Object> attributes = objectMapper.readValue(responseBody, new TypeReference<>() {});

            TwitterUserInfo userInfo = new TwitterUserInfo(attributes);

            String socialId = userInfo.getId();
            String email = userInfo.getEmail();
            String name = userInfo.getUsername();
            String picture = userInfo.getPicture();

            return userRepository.findBySocialIdAndActivatedIsTrue(socialId)
                .orElseGet(() -> userRepository.save(
                    User.createUser(socialId, email, name, picture)
                ));
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("트위터 정보를 가져오는데 실패했습니다.");
        }
    }

}
