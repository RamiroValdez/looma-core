package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.domain.model.User;

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
        user.setMoney(entity.getMoney());
        return user;
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setSurname(user.getSurname());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPhoto(user.getPhoto());
        entity.setMoney(user.getMoney());
        return entity;
    }
}
