package com.amool.hexagonal.application.port.out;

import java.math.BigDecimal;

public interface UserBalancePort {
    void addMoney(Long userId, BigDecimal amount);
}
