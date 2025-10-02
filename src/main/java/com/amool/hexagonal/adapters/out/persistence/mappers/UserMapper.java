package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.UserEntity;
import com.amool.hexagonal.domain.model.User;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setSurname(entity.getSurname());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPhoto(entity.getPhoto());
        return user;
    }
}
