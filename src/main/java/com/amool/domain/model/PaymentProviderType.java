package com.amool.domain.model;

public enum PaymentProviderType {
    MERCADOPAGO;

    public static PaymentProviderType fromString(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase()) {
            case "mercadopago", "mp" -> MERCADOPAGO;
            default -> throw new IllegalArgumentException("Unknown payment provider: " + value);
        };
    }
}
