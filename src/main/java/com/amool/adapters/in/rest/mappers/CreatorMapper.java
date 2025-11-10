package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.CreatorDto;
import com.amool.domain.model.User;

public class CreatorMapper {

    public static CreatorDto toDto(User user) {
        if (user == null) {
            return null;
        }
        CreatorDto dto = new CreatorDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setUsername(user.getUsername());
        dto.setPhoto(user.getPhoto());
        return dto;
    }
}
