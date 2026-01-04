package com.coachcoach.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private boolean success = true;     // 성공 여부

    private String message;             // 응답 메시지(선택)

    private T data;                     //실제 데이터

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();      // 응답 시간

    @JsonIgnore
    @Builder.Default
    private HttpStatus status = HttpStatus.OK;

    // Data
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .status(HttpStatus.OK)
                .build();
    }

    // Data + Message
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(HttpStatus.OK)
                .build();
    }

    // Message - Data X
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .status(status)
                .build();
    }

    public HttpStatus httpStatus() {
        return status;
    }
}
