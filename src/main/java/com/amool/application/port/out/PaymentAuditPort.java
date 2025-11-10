package com.amool.application.port.out;

public interface PaymentAuditPort {
    /**
     * Mark a payment as processed if it's the first time. Returns true if marked now; false if it already existed.
     */
    boolean markProcessedIfFirst(String paymentId);
}
