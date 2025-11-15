package com.amool.application.port.out;

import com.amool.domain.model.PaymentRecord;

import java.util.Optional;

public interface PaymentRecordPort {
    void save(PaymentRecord record);

    Optional<PaymentRecord> findLatestByExternalReference(String externalReference);

    Optional<PaymentRecord> findBySessionUuid(String sessionUuid);

    boolean updateSessionUuidByExternalReference(String externalReference, String sessionUuid);
}
