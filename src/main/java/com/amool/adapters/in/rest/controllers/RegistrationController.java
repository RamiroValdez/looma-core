package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.RegisterRequest;
import com.amool.adapters.in.rest.dtos.VerifyCodeRequest;
import com.amool.application.usecases.StartRegistrationUseCase;
import com.amool.application.usecases.VerifyRegistrationUseCase;
import com.amool.application.usecases.GetUserByIdUseCase;
import com.amool.adapters.in.rest.dtos.AuthResponse;
import com.amool.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final StartRegistrationUseCase startRegistrationUseCase;
    private final VerifyRegistrationUseCase verifyRegistrationUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final JwtService jwtService;

    public RegistrationController(StartRegistrationUseCase startRegistrationUseCase,
                                  VerifyRegistrationUseCase verifyRegistrationUseCase,
                                  GetUserByIdUseCase getUserByIdUseCase,
                                  JwtService jwtService) {
        this.startRegistrationUseCase = startRegistrationUseCase;
        this.verifyRegistrationUseCase = verifyRegistrationUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        startRegistrationUseCase.execute(req.name(), req.surname(), req.username(), req.email(), req.password(), req.confirmPassword());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/register/verify")
    public ResponseEntity<AuthResponse> verify(@Valid @RequestBody VerifyCodeRequest req) {
        Long userId = verifyRegistrationUseCase.execute(req.email(), req.code());
        var userOpt = getUserByIdUseCase.execute(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        var user = userOpt.get();
        var claims = new java.util.HashMap<String, Object>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("surname", user.getSurname());
        claims.put("username", user.getUsername());
        String token = jwtService.generateToken(claims);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }
}
