package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.UpdateUserDto;
import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.adapters.in.rest.mappers.UserRestMapper;
import com.amool.application.usecases.GetUserById;
import com.amool.application.usecases.UpdateUser;
import com.amool.security.JwtUserPrincipal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.amool.application.usecases.SetUserPreferences;
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

    private final GetUserById getUserById;
    private final UpdateUser updateUser;
    private final SetUserPreferences setUserPreferences;

    public UserController(GetUserById getUserById, UpdateUser updateUser,
                          SetUserPreferences setUserPreferences) {
        this.getUserById = getUserById;
        this.updateUser = updateUser;
        this.setUserPreferences = setUserPreferences;
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        return getUserById.execute(id)
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

        boolean ok = updateUser.execute(
                UserRestMapper.updateUserToDomain(form),
                newPassword);

        if (!ok) {
            return ResponseEntity.badRequest().build();
        }

        return getUserById.execute(form.getId())
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
        setUserPreferences.execute(principal.getUserId(), req.genres());
        return ResponseEntity.accepted().build();
    }

}
