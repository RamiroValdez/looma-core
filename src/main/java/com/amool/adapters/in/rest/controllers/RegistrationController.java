package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.RegisterRequest;
import com.amool.adapters.in.rest.dtos.VerifyCodeRequest;
import com.amool.application.usecases.StartRegistration;
import com.amool.application.usecases.VerifyRegistration;
import com.amool.application.usecases.GetUserById;
import com.amool.adapters.in.rest.dtos.AuthResponse;
import com.amool.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final StartRegistration startRegistration;
    private final VerifyRegistration verifyRegistration;
    private final GetUserById getUserById;
    private final JwtService jwtService;

    public RegistrationController(StartRegistration startRegistration,
                                  VerifyRegistration verifyRegistration,
                                  GetUserById getUserById,
                                  JwtService jwtService) {
        this.startRegistration = startRegistration;
        this.verifyRegistration = verifyRegistration;
        this.getUserById = getUserById;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        startRegistration.execute(req.name(), req.surname(), req.username(), req.email(), req.password(), req.confirmPassword());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/register/verify")
    public ResponseEntity<AuthResponse> verify(@Valid @RequestBody VerifyCodeRequest req) {
        Long userId = verifyRegistration.execute(req.email(), req.code());
        var userOpt = getUserById.execute(userId);
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
