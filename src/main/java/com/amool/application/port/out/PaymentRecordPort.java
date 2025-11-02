package com.amool.application.port.out;

import com.amool.domain.model.PaymentRecord;

public interface PaymentRecordPort {
    void save(PaymentRecord record);
}
