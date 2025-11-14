package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.UpdateUserDto;
import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.adapters.in.rest.mappers.UserRestMapper;
import com.amool.application.usecases.GetUserByIdUseCase;
import com.amool.application.usecases.UpdateUserUseCase;
import com.amool.security.JwtUserPrincipal;

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

    @PostMapping("/update")
    public ResponseEntity<UserDto> update(@RequestBody UpdateUserDto userDto,
                                          @AuthenticationPrincipal JwtUserPrincipal userDetails) {
        if(userDetails.getUserId() != userDto.getId()) {
            return ResponseEntity.badRequest().build();
        }
        boolean result = updateUserUseCase.execute(UserRestMapper.updateUserToDomain(userDto), userDto.getNewPassword());
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
