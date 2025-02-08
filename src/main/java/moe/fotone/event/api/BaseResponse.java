package moe.fotone.event.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BaseResponse<T>{
    private final int code;
    private final String message;
    private final T data;

    private BaseResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> OK(T data) {
        return new BaseResponse<>(HttpStatus.OK, "요청이 성공적으로 처리되었습니다.", data);
    }
}
