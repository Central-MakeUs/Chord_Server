package com.coachcoach.gateway.filter;

import com.coachcoach.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseWrapperFilter implements WebFilter {

    private final ObjectMapper objectMapper;

    public ResponseWrapperFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info(">>> Filter START - Path: {}", path);

        if (isSwaggerPath(path)) {
            log.info(">>> Swagger path, skip wrapping");
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                log.info(">>> writeWith called");

                if(body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBuffer joinedBuffer = bufferFactory.join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        DataBufferUtils.release(joinedBuffer);

                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        log.info(">>> Original response: {}", responseBody);

                        HttpStatus statusCode = (HttpStatus) getDelegate().getStatusCode();
                        log.info(">>> Status code: {}", statusCode);

                        // 에러 응답인 경우 래핑 X
                        if (statusCode != null && statusCode.isError()) {
                            log.info(">>> Error response, skip wrapping");
                            return bufferFactory.wrap(content);
                        }

                        // 이미 래핑이 되어 있는 경우
                        if(isAlreadyWrapped(responseBody)) {
                            log.info(">>> Already wrapped, skip");
                            return bufferFactory.wrap(content);
                        }

                        // 래핑
                        try {
                            log.info(">>> START wrapping");
                            Object originalData = objectMapper.readValue(responseBody, Object.class);
                            log.info(">>> Parsed data: {}", originalData);

                            ApiResponse<?> wrappedResponse = ApiResponse.success(originalData);
                            log.info(">>> Created ApiResponse: success={}, message={}",
                                    wrappedResponse.isSuccess(),
                                    wrappedResponse.getMessage());

                            byte[] wrappedBytes = objectMapper.writeValueAsBytes(wrappedResponse);
                            String wrappedJson = new String(wrappedBytes, StandardCharsets.UTF_8);
                            log.info(">>> Wrapped JSON: {}", wrappedJson);

                            // Content-Length 업데이트
                            originalResponse.getHeaders().setContentLength(wrappedBytes.length);
                            log.info(">>> Wrapping SUCCESS");

                            return bufferFactory.wrap(wrappedBytes);
                        } catch (Exception e) {
                            log.error(">>> Wrapping FAILED", e);
                            return bufferFactory.wrap(content);
                        }
                    }));
                }

                log.warn(">>> Body is not Flux");
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private boolean isSwaggerPath(String path) {
        return path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars/");
    }

    private boolean isAlreadyWrapped(String responseBody) {
        try {
            JsonNode node = objectMapper.readTree(responseBody);
            boolean wrapped = node.has("success");
            log.info(">>> isAlreadyWrapped check: {}", wrapped);
            return wrapped;
        } catch (Exception e) {
            log.warn(">>> JSON parse failed in isAlreadyWrapped", e);
            return false;
        }
    }
}