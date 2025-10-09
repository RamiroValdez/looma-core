package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.LoginRequest;
import com.amool.hexagonal.adapters.in.rest.dtos.UserDto;
import com.amool.hexagonal.adapters.in.rest.dtos.AuthResponse;
import com.amool.hexagonal.application.port.in.CredentialsService;
import com.amool.hexagonal.security.JwtService;
import com.amool.hexagonal.security.JwtUserPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final CredentialsService credentialsService;
    private final JwtService jwtService;

    public LoginController(CredentialsService credentialsService, JwtService jwtService) {
        this.credentialsService = credentialsService;
        this.jwtService = jwtService;
    }

    

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        UserDto dto = new UserDto();
        dto.setId(principal.getUserId());
        dto.setEmail(principal.getEmail());
        dto.setName(principal.getName());
        dto.setSurname(principal.getSurname());
        dto.setUsername(principal.getUsername());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return credentialsService.login(request.getEmail(), request.getPassword())
                .map(user -> {
                    var claims = new java.util.HashMap<String, Object>();
                    claims.put("userId", user.getId());
                    claims.put("email", user.getEmail());
                    claims.put("name", user.getName());
                    claims.put("surname", user.getSurname());
                    claims.put("username", user.getUsername());
                    String token = jwtService.generateToken(claims);
                    return ResponseEntity.ok(
                            new AuthResponse(
                                    token,
                                    user.getId(),
                                    user.getEmail(),
                                    user.getName(),
                                    user.getSurname(),
                                    user.getUsername()
                            )
                    );
                })
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
