package com.amool.adapters.out.payment;

import com.amool.adapters.out.payment.MercadoPagoProviderAdapter;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

public class MercadoPagoProviderAdapterTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private UserQueryPort userQueryPort;

    private MercadoPagoProviderAdapter adapter;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        userQueryPort = Mockito.mock(UserQueryPort.class);
        adapter = new MercadoPagoProviderAdapter(restTemplate, obtainWorkByIdPort, loadChapterPort, userQueryPort);
        ReflectionTestUtils.setField(adapter, "accessToken", "test_access_token");
        ReflectionTestUtils.setField(adapter, "apiBase", "https://api.mercadopago.com");
        ReflectionTestUtils.setField(adapter, "successUrl", "http://localhost:5173/return");
        ReflectionTestUtils.setField(adapter, "cancelUrl", "http://localhost:5173/cancel");
        ReflectionTestUtils.setField(adapter, "currency", "ARS");
        ReflectionTestUtils.setField(adapter, "authorPrice", new BigDecimal("1000"));
    }

    @Test
    void startCheckout_withHttpReturnUrl_buildsBackUrlsWithoutAutoReturn() {
        Mockito.when(userQueryPort.existsById(2L)).thenReturn(true);
        Mockito.when(userQueryPort.findNameById(2L)).thenReturn("Autor X");

        server.expect(once(), requestTo("https://api.mercadopago.com/checkout/preferences"))
                .andExpect(method(POST))
                .andRespond(withSuccess("{\n  \"id\": \"pref_123\",\n  \"init_point\": \"https://mp/init\"\n}", MediaType.APPLICATION_JSON));

        PaymentInitResult result = adapter.startCheckout(1L, SubscriptionType.AUTHOR, 2L, "http://localhost:5173/work/1");
        assertThat(result).isNotNull();
        assertThat(result.getRedirectUrl()).isEqualTo("https://mp/init");
        server.verify();
    }

    @Test
    void startCheckout_withHttpsReturnUrl_setsAutoReturn() {
        Mockito.when(userQueryPort.existsById(2L)).thenReturn(true);
        Mockito.when(userQueryPort.findNameById(2L)).thenReturn("Autor X");

        server.expect(once(), requestTo("https://api.mercadopago.com/checkout/preferences"))
                .andExpect(method(POST))
                .andRespond(withSuccess("{\n  \"id\": \"pref_456\",\n  \"init_point\": \"https://mp/init2\"\n}", MediaType.APPLICATION_JSON));

        PaymentInitResult result = adapter.startCheckout(1L, SubscriptionType.AUTHOR, 2L, "https://example.com/return");
        assertThat(result.getRedirectUrl()).isEqualTo("https://mp/init2");
        server.verify();
    }

    @Test
    void startCheckout_workValidatesOwnerAndTitle() {
        Work w = new Work();
        w.setId(10L); w.setTitle("Mi Obra"); w.setPrice(BigDecimal.valueOf(2000.0));
        User creator = new User();
        creator.setId(99L);
        w.setCreator(creator);
        Mockito.when(obtainWorkByIdPort.obtainWorkById(10L)).thenReturn(java.util.Optional.of(w));

        server.expect(once(), requestTo("https://api.mercadopago.com/checkout/preferences"))
                .andExpect(method(POST))
                .andRespond(withSuccess("{\n  \"id\": \"pref_789\",\n  \"init_point\": \"https://mp/init3\"\n}", MediaType.APPLICATION_JSON));

        PaymentInitResult result = adapter.startCheckout(1L, SubscriptionType.WORK, 10L);
        assertThat(result.getExternalReference()).isEqualTo("pref_789");
        server.verify();
    }

    @Test
    void startCheckout_chapterResolvesWorkAndTitle() {
        Chapter c = new Chapter();
        c.setId(7L); c.setTitle("Cap 1"); c.setPrice(BigDecimal.valueOf(300.0)); c.setWorkId(10L);
        Mockito.when(loadChapterPort.loadChapterForEdit(7L)).thenReturn(java.util.Optional.of(c));
        Work w = new Work(); w.setId(10L); w.setTitle("Mi Obra"); User cr = new User(); cr.setId(55L); w.setCreator(cr);
        Mockito.when(obtainWorkByIdPort.obtainWorkById(10L)).thenReturn(java.util.Optional.of(w));

        server.expect(once(), requestTo("https://api.mercadopago.com/checkout/preferences"))
                .andExpect(method(POST))
                .andRespond(withSuccess("{\n  \"id\": \"pref_321\",\n  \"init_point\": \"https://mp/init4\"\n}", MediaType.APPLICATION_JSON));

        PaymentInitResult result = adapter.startCheckout(2L, SubscriptionType.CHAPTER, 7L);
        assertThat(result.getExternalReference()).isEqualTo("pref_321");
        server.verify();
    }
}
