package com.amool.adapters.out.payment;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.PaymentSessionLinkPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import com.amool.application.port.out.UserQueryPort;
import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.SubscriptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;
import com.amool.application.port.out.LoadUserPort;

public class MercadoPagoProviderAdapterTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private UserQueryPort userQueryPort;
    private LoadUserPort loadUserPort;
    private PaymentSessionLinkPort paymentSessionLinkPort;

    private MercadoPagoProviderAdapter adapter;
    private static final String PREFERENCES_URL = "https://api.mercadopago.com/checkout/preferences";

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        userQueryPort = Mockito.mock(UserQueryPort.class);
        loadUserPort = Mockito.mock(LoadUserPort.class);
        paymentSessionLinkPort = Mockito.mock(PaymentSessionLinkPort.class);
        adapter = new MercadoPagoProviderAdapter(restTemplate, obtainWorkByIdPort, loadChapterPort, userQueryPort, loadUserPort, paymentSessionLinkPort);
        ReflectionTestUtils.setField(adapter, "accessToken", "test_access_token");
        ReflectionTestUtils.setField(adapter, "apiBase", "https://api.mercadopago.com");
        ReflectionTestUtils.setField(adapter, "successUrl", "http://localhost:5173/return");
        ReflectionTestUtils.setField(adapter, "cancelUrl", "http://localhost:5173/cancel");
        ReflectionTestUtils.setField(adapter, "currency", "ARS");
    }

    @Test
    void startCheckout_withHttpReturnUrl_buildsBackUrlsWithoutAutoReturn() {
        givenAuthorExists(2L, "Autor X", new BigDecimal("1000"));
        expectPreferenceCreation("pref_123", "https://mp/init");

        PaymentInitResult result = startAuthorCheckout(1L, 2L, "http://localhost:5173/work/1");

        thenRedirectUrlIs(result, "https://mp/init");
        thenPreferenceCreationVerified();
    }

    @Test
    void startCheckout_withHttpsReturnUrl_setsAutoReturn() {
        givenAuthorExists(2L, "Autor X", new BigDecimal("1200"));
        expectPreferenceCreation("pref_456", "https://mp/init2");

        PaymentInitResult result = startAuthorCheckout(1L, 2L, "https://example.com/return");

        thenRedirectUrlIs(result, "https://mp/init2");
        thenPreferenceCreationVerified();
    }

    @Test
    void startCheckout_workValidatesOwnerAndTitle() {
        givenWorkExists(10L, "Mi Obra", BigDecimal.valueOf(2000.0), 99L);
        expectPreferenceCreation("pref_789", "https://mp/init3");

        PaymentInitResult result = startWorkCheckout(1L, 10L);

        thenExternalReferenceIs(result, "pref_789");
        thenPreferenceCreationVerified();
    }

    @Test
    void startCheckout_chapterResolvesWorkAndTitle() {
        givenChapterExists(7L, 10L, "Cap 1", BigDecimal.valueOf(300.0));
        givenWorkExists(10L, "Mi Obra", null, 55L);
        expectPreferenceCreation("pref_321", "https://mp/init4");

        PaymentInitResult result = startChapterCheckout(2L, 7L);

        thenExternalReferenceIs(result, "pref_321");
        thenPreferenceCreationVerified();
    }

    private void givenAuthorExists(long authorId, String authorName, BigDecimal price) {
        Mockito.when(userQueryPort.existsById(authorId)).thenReturn(true);
        Mockito.when(userQueryPort.findNameById(authorId)).thenReturn(authorName);
        User author = new User();
        author.setId(authorId);
        author.setPrice(price);
        Mockito.when(loadUserPort.getById(authorId)).thenReturn(Optional.of(author));
    }

    private void givenWorkExists(long workId, String title, BigDecimal price, long creatorId) {
        Work work = new Work();
        work.setId(workId);
        work.setTitle(title);
        if (price != null) {
            work.setPrice(price);
        }
        User creator = new User();
        creator.setId(creatorId);
        work.setCreator(creator);
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
    }

    private void givenChapterExists(long chapterId, long workId, String title, BigDecimal price) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        chapter.setTitle(title);
        chapter.setPrice(price);
        Mockito.when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(chapter));
    }

    private void expectPreferenceCreation(String preferenceId, String initPointUrl) {
        String payload = String.format("{\n  \"id\": \"%s\",\n  \"init_point\": \"%s\"\n}", preferenceId, initPointUrl);
        server.expect(once(), requestTo(PREFERENCES_URL))
                .andExpect(method(POST))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
    }

    private PaymentInitResult startAuthorCheckout(long buyerId, long authorId, String returnUrl) {
        return adapter.startCheckout(buyerId, SubscriptionType.AUTHOR, authorId, returnUrl);
    }

    private PaymentInitResult startWorkCheckout(long buyerId, long workId) {
        return adapter.startCheckout(buyerId, SubscriptionType.WORK, workId);
    }

    private PaymentInitResult startChapterCheckout(long buyerId, long chapterId) {
        return adapter.startCheckout(buyerId, SubscriptionType.CHAPTER, chapterId);
    }

    private void thenRedirectUrlIs(PaymentInitResult result, String expectedUrl) {
        assertThat(result).isNotNull();
        assertThat(result.getRedirectUrl()).isEqualTo(expectedUrl);
    }

    private void thenExternalReferenceIs(PaymentInitResult result, String expectedReference) {
        assertThat(result.getExternalReference()).isEqualTo(expectedReference);
    }

    private void thenPreferenceCreationVerified() {
        server.verify();
    }
}
