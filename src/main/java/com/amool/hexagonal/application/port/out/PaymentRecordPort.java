package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.PaymentRecord;

public interface PaymentRecordPort {
    void save(PaymentRecord record);
}
