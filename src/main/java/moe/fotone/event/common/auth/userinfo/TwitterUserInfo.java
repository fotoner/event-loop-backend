package moe.fotone.event.common.auth.userinfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TwitterUserInfo extends OAuth2UserInfo{
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {};

    public TwitterUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    private Map<String, Object> convertToMap(Object object) {
        return objectMapper.convertValue(object, typeReference);
    }

    private Map<String, Object> getData() {
        return convertToMap(attributes.get("data"));
    }

    @Override
    public String getId() {
        Map<String, Object> data = getData();
        return (String) data.get("id");
    }

    @Override
    public String getEmail() {
        Map<String, Object> data = getData();
        return (String) data.get("id");
    }

    @Override
    public String getUsername() {
        Map<String, Object> data = getData();
        return (String) data.get("username");
    }

    @Override
    public String getPicture() {
        Map<String, Object> data = getData();
        return (String) data.get("id");
    }
}
