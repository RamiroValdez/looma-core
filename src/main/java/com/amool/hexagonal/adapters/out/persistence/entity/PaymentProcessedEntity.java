package com.amool.hexagonal.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_processed")
public class PaymentProcessedEntity {

    @Id
    @Column(name = "payment_id", nullable = false, length = 64)
    private String paymentId;

    public PaymentProcessedEntity() {}
    public PaymentProcessedEntity(String paymentId) { this.paymentId = paymentId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
