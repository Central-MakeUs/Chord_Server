package com.coachcoach.gateway.exception;

import com.coachcoach.common.dto.ErrorResponse;
import com.coachcoach.common.exception.CommonErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-1)
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Gateway error occurred: ", ex);

        ErrorResponse errorResponse;
        HttpStatus httpStatus;

        log.info("Gateway error occurred: ", ex);

        // ResponseStatusException 처리 (Gateway 라우팅 오류 등)
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            httpStatus = HttpStatus.valueOf(rse.getStatusCode().value());

            // 서비스 연결 실패
            if (httpStatus == HttpStatus.SERVICE_UNAVAILABLE || httpStatus == HttpStatus.GATEWAY_TIMEOUT) {
                errorResponse = ErrorResponse.of(CommonErrorCode.SERVICE_UNAVAILABLE);
            }

            // 라우팅 실패 (404)
            else if (httpStatus == HttpStatus.NOT_FOUND) {
                errorResponse = ErrorResponse.of(CommonErrorCode.NOT_FOUND);
            }

            // 기타 ResponseStatusException
            else {
                log.error("Unexpected gateway error occurred: ", ex);
                errorResponse = ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 일반 예외 처리
        else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return writeErrorResponse(exchange, errorResponse, httpStatus);
    }


    private Mono<Void> writeErrorResponse(
            ServerWebExchange exchange,
            ErrorResponse errorResponse,
            HttpStatus httpStatus
    ) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            return writeFallbackError(exchange);
        }
    }

    private Mono<Void> writeFallbackError(ServerWebExchange exchange) {
        String fallbackJson = """
            {
                "success": false,
                "code": "COMMON_003",
                "message": "서버 내부 오류가 발생했습니다",
                "timestamp": "%s"
            }
            """.formatted(java.time.LocalDateTime.now());

        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(fallbackJson.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}