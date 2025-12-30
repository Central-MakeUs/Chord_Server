package com.coachcoach.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @GetMapping("/api/auth/hi")
    public String hi(){
        return "hi";
    }
}
