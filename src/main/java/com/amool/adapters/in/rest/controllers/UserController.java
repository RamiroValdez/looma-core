package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.UpdateUserDto;
import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.adapters.in.rest.mappers.UserRestMapper;
import com.amool.application.usecases.GetUserByIdUseCase;
import com.amool.application.usecases.UpdateUserUseCase;
import com.amool.security.JwtUserPrincipal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.amool.application.usecases.SetUserPreferencesUseCase;
import com.amool.adapters.in.rest.dtos.PreferencesRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final SetUserPreferencesUseCase setUserPreferencesUseCase;

    public UserController(GetUserByIdUseCase getUserByIdUseCase, UpdateUserUseCase updateUserUseCase,
            SetUserPreferencesUseCase setUserPreferencesUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.setUserPreferencesUseCase = setUserPreferencesUseCase;
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        return getUserByIdUseCase.execute(id)
                .map(UserRestMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(@ModelAttribute UpdateUserDto form,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        if (!principal.getUserId().equals(form.getId())) {
            return ResponseEntity.badRequest().build();
        }

        String newPassword = form.hasNewPassword() ? form.getNewPassword() : null;

        boolean ok = updateUserUseCase.execute(
                UserRestMapper.updateUserToDomain(form),
                newPassword);

        if (!ok) {
            return ResponseEntity.badRequest().build();
        }

        return getUserByIdUseCase.execute(form.getId())
                .map(UserRestMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/preferences")
    public ResponseEntity<Void> setPreferences(@AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody PreferencesRequest req) {
        if (principal == null || principal.getUserId() == null) {
            return ResponseEntity.status(401).build();
        }
        setUserPreferencesUseCase.execute(principal.getUserId(), req.genres());
        return ResponseEntity.accepted().build();
    }

}
