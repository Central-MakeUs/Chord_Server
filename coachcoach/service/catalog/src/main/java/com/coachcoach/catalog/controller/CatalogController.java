package com.coachcoach.catalog.controller;

import com.coachcoach.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @RequestHeader Map<String, String> headers → headers.get("userId")
 */

@RestController
@RequiredArgsConstructor
public class CatalogController {
    @GetMapping(path = "/hi")
    public ApiResponse<Void> hi(@RequestHeader Map<String, String> headers) {
        return ApiResponse.success(headers.get("user_id"));
    }
}
