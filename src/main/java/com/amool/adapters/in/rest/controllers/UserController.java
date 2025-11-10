package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.adapters.in.rest.mappers.UserRestMapper;
import com.amool.application.usecases.GetUserByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;

    public UserController(GetUserByIdUseCase getUserByIdUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
    }
    

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        return getUserByIdUseCase.execute(id)
                .map(UserRestMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
