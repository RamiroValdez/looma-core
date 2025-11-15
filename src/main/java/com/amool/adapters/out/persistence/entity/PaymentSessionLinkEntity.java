package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment_session_link")
public class PaymentSessionLinkEntity {

    @Id
    @Column(name = "external_reference", length = 128, nullable = false)
    private String externalReference;

    @Column(name = "session_uuid", length = 64)
    private String sessionUuid;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }
    public String getSessionUuid() { return sessionUuid; }
    public void setSessionUuid(String sessionUuid) { this.sessionUuid = sessionUuid; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
