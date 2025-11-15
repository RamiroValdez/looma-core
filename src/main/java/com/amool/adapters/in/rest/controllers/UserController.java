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


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserController(GetUserByIdUseCase getUserByIdUseCase, UpdateUserUseCase updateUserUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
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

        // Normalizar password vacío
        String newPassword = form.hasNewPassword() ? form.getNewPassword() : null;

        boolean ok = updateUserUseCase.execute(
                UserRestMapper.updateUserToDomain(form),
                newPassword
                // Si el caso de uso soporta archivo, agregar form.getFile()
        );

        if (!ok) {
            return ResponseEntity.badRequest().build();
        }

        return getUserByIdUseCase.execute(form.getId())
                .map(UserRestMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
