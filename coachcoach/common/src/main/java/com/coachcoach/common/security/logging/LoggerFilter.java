package com.coachcoach.common.security.logging;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class LoggerFilter implements Filter {

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = "anonymous";
        Long userId = null;

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            username = details.getLoginId();
            userId = Long.valueOf(details.getUserId());
        }

        String method = req.getMethod();
        String uri = req.getRequestURI();
        int status = res.getStatus();

        log.info("[{}({})] {} {} → {}", username, userId, method, uri, status);

        if(!reqJson.isBlank()){
            log.info("Request: {}", reqJson);
        }

        // 응답 데이터 복원
        res.copyBodyToResponse();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
