package com.amool.application.port.out;

public interface UserQueryPort {
    boolean existsById(Long userId);
    String findNameById(Long userId);
}
