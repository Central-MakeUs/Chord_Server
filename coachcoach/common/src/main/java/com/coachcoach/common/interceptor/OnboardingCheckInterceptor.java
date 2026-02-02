package com.coachcoach.common.interceptor;

import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.dto.internal.UserInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.CommonErrorCode;
import com.coachcoach.common.security.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnboardingCheckInterceptor implements HandlerInterceptor {

    private final UserQueryApi userQueryApi;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String requestURI = request.getRequestURI();

        log.debug("==================== ONBOARDING CHECK====================");
        log.debug(" Request URI \t: " + request.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }

        Object principal = authentication.getPrincipal();


        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;

            UserInfo user = userQueryApi.findUserByUserId(Long.valueOf(userDetails.getUserId()));

            if(!user.onboardingCompleted())
                throw new BusinessException(CommonErrorCode.ONBOARDING_NOT_COMPLETED);
        }

        return true;
    }
}
