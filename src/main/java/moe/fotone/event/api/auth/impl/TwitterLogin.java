package moe.fotone.event.api.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.userinfo.TwitterUserInfo;
import moe.fotone.event.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
    private static final Logger log = LoggerFactory.getLogger(TwitterLogin.class);

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
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
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

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get token: " + response.getBody());
        }

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }
    @Transactional
    public User getUser(String token) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> profileRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUri + "?user.fields=profile_image_url,url,description",
                    HttpMethod.GET,
                    profileRequest,
                    String.class
            );

            String responseBody = response.getBody();
            log.info(responseBody);
            Map<String, Object> attributes = objectMapper.readValue(responseBody, new TypeReference<>() {});

            TwitterUserInfo userInfo = new TwitterUserInfo(attributes);
            String socialId = userInfo.getId();
            String username = userInfo.getUsername();
            String name = userInfo.getName();
            String picture = userInfo.getPicture();
            String description = userInfo.getDescription();

            return userRepository.findBySocialIdAndActivatedIsTrue(socialId)
                    .orElseGet(() -> userRepository.save(
                            User.createUser(socialId, username, name, description, picture)
                    ));
        } catch (HttpClientErrorException e) {
            log.error("Failed to get user data from Twitter with Authorization header: " + headers.get("Authorization"), e);
            throw new IllegalArgumentException("트위터 정보를 가져오는데 실패했습니다.", e);
        }
    }

}
