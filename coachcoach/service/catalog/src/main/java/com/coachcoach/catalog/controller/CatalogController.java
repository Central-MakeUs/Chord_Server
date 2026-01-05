package com.coachcoach.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


/**
 * @RequestHeader(value = "userId", required = false)로 헤더 GET
 * return 자료형으로 원시 자료형 사용 불가 (무조건 DTO로 래핑 / 참조 자료형 사용)
 */

@RestController
@RequiredArgsConstructor
public class CatalogController {

}
