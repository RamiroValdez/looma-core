package com.amool.adapters.out.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentAuditPersistenceAdapterTest {

    @Autowired
    private PaymentAuditPersistenceAdapter adapter;

    @PersistenceContext
    private EntityManager entityManager;

    private String givenPaymentId(String value) {
        return value;
    }

    private boolean whenMarkProcessed(String paymentId) {
        return adapter.markProcessedIfFirst(paymentId);
    }

    private void thenRowCountForPaymentIdIs(String paymentId, long expectedCount) {
        Long count = ((Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM payment_processed WHERE payment_id = :pid")
                .setParameter("pid", paymentId)
                .getSingleResult()).longValue();
        assertEquals(expectedCount, count);
    }

    private void thenResultIs(boolean actual, boolean expected) {
        assertEquals(expected, actual);
    }

    @Nested
    @DisplayName("markProcessedIfFirst - Validaciones de entrada")
    class InputValidation {
        @Test
        @DisplayName("Given paymentId null, when markProcessedIfFirst, then return false")
        void givenNull_whenMark_thenFalse() {
            String paymentId = givenPaymentId(null);

            boolean result = whenMarkProcessed(paymentId);

            thenResultIs(result, false);
        }

        @Test
        @DisplayName("Given paymentId en blanco, when markProcessedIfFirst, then return false")
        void givenBlank_whenMark_thenFalse() {
            String paymentId = givenPaymentId("   ");

            boolean result = whenMarkProcessed(paymentId);

            thenResultIs(result, false);
        }
    }

    @Nested
    @DisplayName("markProcessedIfFirst - Comportamiento de inserción e idempotencia")
    class InsertionBehavior {
        @Test
        @DisplayName("Given nuevo paymentId, when markProcessedIfFirst, then inserta y retorna true")
        void givenNewId_whenMark_thenInsertAndTrue() {
            String paymentId = givenPaymentId("pay-001");

            boolean result = whenMarkProcessed(paymentId);
            thenResultIs(result, true);
            thenRowCountForPaymentIdIs(paymentId, 1);
        }

        @Test
        @DisplayName("Given paymentId ya procesado, when markProcessedIfFirst, then no inserta y retorna false")
        void givenExistingId_whenMark_thenNoInsertAndFalse() {
            String paymentId = givenPaymentId("pay-002");

            boolean first = whenMarkProcessed(paymentId);
            thenResultIs(first, true);
            thenRowCountForPaymentIdIs(paymentId, 1);

            boolean second = whenMarkProcessed(paymentId);

            thenResultIs(second, false);
            thenRowCountForPaymentIdIs(paymentId, 1);
        }
    }
}
