package com.amool.application.port.out;

import java.util.Optional;

public interface PaymentSessionLinkPort {
    void saveLink(String externalReference, String sessionUuid);
    Optional<String> findSessionUuid(String externalReference);
}
