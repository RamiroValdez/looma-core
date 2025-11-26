package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.PaymentRecordEntity;
import com.amool.domain.model.PaymentRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentRecordPersistenceAdapterTest {

    @Autowired
    private PaymentRecordPersistenceAdapter adapter;

    @PersistenceContext
    private EntityManager em;

    private PaymentRecord givenPaymentRecord(String id, String externalRef, String sessionUuid, Instant createdAt) {
        PaymentRecord r = new PaymentRecord();
        r.setId(id);
        r.setUserId(1L);
        r.setTitle("Title");
        r.setProvider("MERCADOPAGO");
        r.setAmount(new BigDecimal("9.99"));
        r.setCurrency("ARS");
        r.setPaymentMethod("CARD");
        r.setStatus("APPROVED");
        r.setSubscriptionType("PREMIUM");
        r.setTargetId(1L);
        r.setCreatedAt(createdAt != null ? OffsetDateTime.ofInstant(createdAt, ZoneOffset.UTC) : null);
        r.setExternalReference(externalRef);
        r.setSessionUuid(sessionUuid);
        return r;
    }

    private void whenSave(PaymentRecord record) {
        adapter.save(record);
    }

    private Optional<PaymentRecord> whenFindLatestByExternalReference(String externalReference) {
        return adapter.findLatestByExternalReference(externalReference);
    }

    private Optional<PaymentRecord> whenFindBySessionUuid(String sessionUuid) {
        return adapter.findBySessionUuid(sessionUuid);
    }

    private boolean whenUpdateSessionUuidByExternalReference(String externalRef, String sessionUuid) {
        return adapter.updateSessionUuidByExternalReference(externalRef, sessionUuid);
    }

    private PaymentRecordEntity thenEntityExistsWithId(String id) {
        PaymentRecordEntity e = em.find(PaymentRecordEntity.class, id);
        assertNotNull(e, "Debe existir entidad con id=" + id);
        return e;
    }

    private void thenEntityFieldEquals(PaymentRecordEntity e, String expectedExternalRef, String expectedSessionUuid) {
        assertEquals(expectedExternalRef, e.getExternalReference());
        assertEquals(expectedSessionUuid, e.getSessionUuid());
    }

    private void thenLatestByExternalReferenceIs(String externalRef, String expectedId) {
        Optional<PaymentRecord> opt = whenFindLatestByExternalReference(externalRef);
        assertTrue(opt.isPresent(), "Debe encontrar último registro por externalReference");
        assertEquals(expectedId, opt.get().getId());
    }

    private void thenFindBySessionUuidIs(String sessionUuid, String expectedId) {
        Optional<PaymentRecord> opt = whenFindBySessionUuid(sessionUuid);
        assertTrue(opt.isPresent(), "Debe encontrar por sessionUuid");
        assertEquals(expectedId, opt.get().getId());
    }

    private long countByExternalRef(String externalRef) {
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(e) FROM PaymentRecordEntity e WHERE e.externalReference = :ref", Long.class);
        q.setParameter("ref", externalRef);
        return q.getSingleResult();
    }

    @Nested
    @DisplayName("save - validaciones y persistencia")
    class SaveBehavior {
        @Test
        @DisplayName("Given record null, when save, then no persiste")
        void givenNull_whenSave_thenNoPersist() {

            PaymentRecord record = null;

            whenSave(record);
        }

        @Test
        @DisplayName("Given record sin id, when save, then no persiste")
        void givenNoId_whenSave_thenNoPersist() {
            PaymentRecord record = givenPaymentRecord(null, "ext-1", "sess-1", Instant.now());

            whenSave(record);

            assertEquals(0L, countByExternalRef("ext-1"));
        }

        @Test
        @DisplayName("Given nuevo id, when save, then persiste entidad")
        void givenNewId_whenSave_thenPersist() {
            String id = "pay-100";
            PaymentRecord record = givenPaymentRecord(id, "ext-100", "sess-100", Instant.now());

            whenSave(record);

            PaymentRecordEntity e = thenEntityExistsWithId(id);
            thenEntityFieldEquals(e, "ext-100", "sess-100");
        }

        @Test
        @DisplayName("Given id existente, when save con cambios, then actualiza campos")
        void givenExistingId_whenSave_updatesFields() {
            String id = "pay-200";
            PaymentRecord r1 = givenPaymentRecord(id, "ext-200", "sess-200", Instant.parse("2024-01-01T00:00:00Z"));
            whenSave(r1);

            PaymentRecord r2 = givenPaymentRecord(id, "ext-200-upd", "sess-200-upd", Instant.parse("2024-01-02T00:00:00Z"));
            r2.setAmount(new BigDecimal("19.99"));

            whenSave(r2);

            PaymentRecordEntity e = thenEntityExistsWithId(id);
            assertEquals(new BigDecimal("19.99"), e.getAmount());
            thenEntityFieldEquals(e, "ext-200-upd", "sess-200-upd");
        }
    }

    @Nested
    @DisplayName("findLatestByExternalReference - orden por createdAt DESC")
    class FindLatestByExternalReference {
        @Test
        @DisplayName("Given varios registros con misma referencia, when findLatest, then retorna el más reciente")
        void givenMultipleSameRef_whenFindLatest_thenReturnsMostRecent() {
            String ref = "ext-300";
            PaymentRecord oldR = givenPaymentRecord("pay-301", ref, null, Instant.parse("2024-01-01T00:00:00Z"));
            PaymentRecord newR = givenPaymentRecord("pay-302", ref, null, Instant.parse("2024-02-01T00:00:00Z"));
            whenSave(oldR);
            whenSave(newR);

            thenLatestByExternalReferenceIs(ref, "pay-302");
        }

        @Test
        @DisplayName("Given externalReference null, when findLatest, then empty")
        void givenNullRef_whenFindLatest_thenEmpty() {
            String ref = null;

            Optional<PaymentRecord> opt = whenFindLatestByExternalReference(ref);

            assertTrue(opt.isEmpty());
        }
    }

    @Nested
    @DisplayName("findBySessionUuid - búsqueda simple")
    class FindBySessionUuid {
        @Test
        @DisplayName("Given registro con sessionUuid, when findBySessionUuid, then retorna ese registro")
        void givenRecordWithSessionUuid_whenFind_thenReturn() {
            PaymentRecord r = givenPaymentRecord("pay-400", "ext-400", "sess-400", Instant.now());
            whenSave(r);

            thenFindBySessionUuidIs("sess-400", "pay-400");
        }

        @Test
        @DisplayName("Given sessionUuid null, when findBySessionUuid, then empty")
        void givenNullSession_whenFind_thenEmpty() {
            String sessionUuid = null;

            Optional<PaymentRecord> opt = whenFindBySessionUuid(sessionUuid);

            assertTrue(opt.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateSessionUuidByExternalReference - actualización por última referencia")
    class UpdateSessionByExternalRef {
        @Test
        @DisplayName("Given última referencia existente, when update, then retorna true y actualiza")
        void givenExistingLatestRef_whenUpdate_thenTrueAndUpdates() {
            String ref = "ext-500";
            PaymentRecord r1 = givenPaymentRecord("pay-501", ref, null, Instant.parse("2024-01-01T00:00:00Z"));
            PaymentRecord r2 = givenPaymentRecord("pay-502", ref, null, Instant.parse("2024-03-01T00:00:00Z"));
            whenSave(r1);
            whenSave(r2);

            boolean updated = whenUpdateSessionUuidByExternalReference(ref, "sess-500");

            assertTrue(updated);
            PaymentRecordEntity latest = thenEntityExistsWithId("pay-502");
            assertEquals("sess-500", latest.getSessionUuid());
        }

        @Test
        @DisplayName("Given referencia inexistente, when update, then retorna false")
        void givenMissingRef_whenUpdate_thenFalse() {
            String ref = "missing-ref";

            boolean updated = whenUpdateSessionUuidByExternalReference(ref, "sess-x");

            assertFalse(updated);
        }
    }
}
