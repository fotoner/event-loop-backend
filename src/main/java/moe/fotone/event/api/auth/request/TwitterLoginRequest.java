package moe.fotone.event.api.auth.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TwitterLoginRequest {
    private String code;
}
