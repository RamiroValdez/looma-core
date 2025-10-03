package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.LoginRequest;
import com.amool.hexagonal.adapters.in.rest.dtos.AuthResponse;
import com.amool.hexagonal.application.port.in.LoginUseCase;
import com.amool.hexagonal.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final JwtService jwtService;

    public LoginController(LoginUseCase loginUseCase, JwtService jwtService) {
        this.loginUseCase = loginUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return loginUseCase.login(request.getEmail(), request.getPassword())
                .map(user -> {
                    var claims = new java.util.HashMap<String, Object>();
                    claims.put("userId", user.getId());
                    claims.put("email", user.getEmail());
                    claims.put("name", user.getName());
                    claims.put("surname", user.getSurname());
                    claims.put("username", user.getUsername());
                    String token = jwtService.generateToken(claims);
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
