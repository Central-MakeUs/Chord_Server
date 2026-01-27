package com.coachcoach.user.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class LoginIdValidator implements ConstraintValidator<ValidLoginId, String> {

    // 영문 소문자 + 숫자만, 3-20자
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-z0-9]{3,20}$");

    // 불가능한 경우: 숫자만으로 구성
    private static final Pattern ONLY_NUMBERS_PATTERN = Pattern.compile("^[0-9]+$");

    @Override
    public boolean isValid(String loginId, ConstraintValidatorContext context) {
        if (loginId == null || loginId.isBlank()) {
            // @NotBlank에 처리 위임
            return true;
        }

        //  기본 형식 검증 (영문 소문자 + 숫자, 3-20자)
        if (!LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "아이디는 3-20자의 영문 소문자와 숫자만 사용 가능합니다."
            ).addConstraintViolation();
            return false;
        }

        // 숫자만으로 구성 불가
        if (ONLY_NUMBERS_PATTERN.matcher(loginId).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "아이디는 숫자만으로 구성할 수 없습니다."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}