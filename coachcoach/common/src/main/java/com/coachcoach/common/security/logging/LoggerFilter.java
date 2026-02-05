package com.coachcoach.common.security.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
public class LoggerFilter implements Filter {
    private final ServletRequest servletRequest;

    public LoggerFilter(ServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var req = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        var res = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        // Dispatch 전
        filterChain.doFilter(req, res);

        // Dispatch 후

        // 요청, 응답 데이터 추출
        var reqJson = new String(req.getContentAsByteArray(), servletRequest.getCharacterEncoding());
        var resJson = new String(res.getContentAsByteArray(), servletResponse.getCharacterEncoding());

        log.info("Request: {}", reqJson.isBlank() ? "Empty Request Body" : reqJson);

        // 응답 데이터 복원
        res.copyBodyToResponse();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
