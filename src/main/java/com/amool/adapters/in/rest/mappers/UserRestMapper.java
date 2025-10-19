package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.domain.model.User;

public class UserRestMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoto(user.getPhoto());
        return dto;
        }
}
