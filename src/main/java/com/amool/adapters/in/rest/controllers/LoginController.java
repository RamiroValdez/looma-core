package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.LoginRequest;
import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.adapters.in.rest.dtos.AuthResponse;
import com.amool.application.usecases.GetUserPhoto;
import com.amool.application.usecases.LoginUseCase;
import com.amool.security.JwtService;
import com.amool.security.JwtUserPrincipal;
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

    private final LoginUseCase loginUseCase;
    private final JwtService jwtService;
    private final GetUserPhoto getUserPhoto;

    public LoginController(LoginUseCase loginUseCase, JwtService jwtService, GetUserPhoto getUserPhoto) {
        this.loginUseCase = loginUseCase;
        this.jwtService = jwtService;
        this.getUserPhoto = getUserPhoto;
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
        dto.setPhoto(getUserPhoto.execute(principal.getUserId()));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return loginUseCase.execute(request.getEmail(), request.getPassword())
                .map(user -> {
                    var claims = new java.util.HashMap<String, Object>();
                    claims.put("userId", user.getId());
                    claims.put("email", user.getEmail());
                    claims.put("name", user.getName());
                    claims.put("surname", user.getSurname());
                    claims.put("username", user.getUsername());
                    String token = jwtService.generateToken(claims);
                    return ResponseEntity.ok(
                            new AuthResponse(token)
                    );
                })
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
