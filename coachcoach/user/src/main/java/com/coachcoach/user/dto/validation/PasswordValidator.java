package com.coachcoach.user.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // 영문 대소문자 + 숫자 + 특수문자만 허용
    private static final Pattern ALLOWED_CHARS_PATTERN =
            Pattern.compile("^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$");

    // 소문자 포함 여부
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");

    // 대문자 포함 여부
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");

    // 숫자 포함 여부
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");

    // 특수문자 포함 여부
    private static final Pattern SPECIAL_CHAR_PATTERN =
            Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true;
        }

        // 길이 검증
        if (password.length() < 8 || password.length() > 100) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호는 8-100자 사이여야 합니다."
            ).addConstraintViolation();
            return false;
        }

        // 허용된 문자만 사용 (한글 X)
        if (!ALLOWED_CHARS_PATTERN.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호는 영문 대소문자, 숫자, 특수문자만 사용 가능합니다."
            ).addConstraintViolation();
            return false;
        }

        // 대소문자, 숫자, 특수문자 중 3가지 이상 포함
        int typesCount = 0;
        if (LOWERCASE_PATTERN.matcher(password).find()) typesCount++;
        if (UPPERCASE_PATTERN.matcher(password).find()) typesCount++;
        if (DIGIT_PATTERN.matcher(password).find()) typesCount++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) typesCount++;

        if (typesCount < 3) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호는 대소문자, 숫자, 특수문자 중 3가지 이상을 포함해야 합니다."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
