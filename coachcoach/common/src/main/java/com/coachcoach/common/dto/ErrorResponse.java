package com.coachcoach.common.dto;

import com.coachcoach.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Builder.Default
    private boolean success = false;                // 실패

    private String code;                            // 에러 코드

    private String message;                         // 에러 메시지

    private Map<String, String> errors;             // Validation 에러

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();      // 응답 시간

    // ErrorCode Enum 활용 에러
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    // code + message 직접 작성
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    // Validation 에러
    public static ErrorResponse of(String code, String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}
