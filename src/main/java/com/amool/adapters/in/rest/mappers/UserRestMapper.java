package com.amool.adapters.in.rest.mappers;

import java.io.IOException;
import java.math.BigDecimal;

import com.amool.adapters.in.rest.dtos.UpdateUserDto;
import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.domain.model.InMemoryMultipartFile;
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
        dto.setMoney(user.getMoney().toString());
        dto.setPrice(user.getPrice());
        return dto;
        }

    public static User updateUserToDomain(UpdateUserDto userDto) {
        if (userDto == null) return null;
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoto(userDto.getPhoto());
        user.setPrice(userDto.getPrice());
        if(userDto.getMoney() != null) {
            user.setMoney(userDto.getMoney());
        }
        try{
            if(userDto.getFile() != null){
                user.setMultipartFile(new InMemoryMultipartFile(userDto.getFile().getName(), userDto.getFile().getName(), userDto.getFile().getContentType(), userDto.getFile().getBytes()));
            }
        } catch (IOException e) {
            user.setMultipartFile(null);
        }
        return user;
    }
}
