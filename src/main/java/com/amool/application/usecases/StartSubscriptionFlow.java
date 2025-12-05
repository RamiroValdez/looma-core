package com.amool.application.usecases;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.PaymentProviderPort;
import com.amool.domain.model.*;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class StartSubscriptionFlow {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterPort loadChapterPort;
    private final SubscribeUser subscribeUser;
    private final Map<PaymentProviderType, PaymentProviderPort> providers = new EnumMap<>(PaymentProviderType.class);;
    private final LoadUserPort loadUserPort;

    public StartSubscriptionFlow(
            ObtainWorkByIdPort obtainWorkByIdPort,
            LoadChapterPort loadChapterPort,
            SubscribeUser subscribeUser,
            List<PaymentProviderPort> providerAdapters,
            LoadUserPort loadUserPort
    ) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterPort = loadChapterPort;
        this.subscribeUser = subscribeUser;
        if (providerAdapters != null) {
            for (PaymentProviderPort p : providerAdapters) {
                this.providers.put(p.supportedProvider(), p);
            }
        }
        this.loadUserPort = loadUserPort;
    }

    public Result execute(Long userId,
                          String subscriptionType,
                          Long targetId,
                          Long workId,
                          String provider,
                          String returnUrl) {
        SubscriptionType type;
        try {
            type = SubscriptionType.fromString(subscriptionType);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid subscriptionType");
        }

        if (type == SubscriptionType.AUTHOR && targetId != null && targetId.equals(userId)) {
            throw new IllegalArgumentException("Cannot subscribe to yourself");
        }

        BigDecimal price = resolvePrice(type, targetId, workId);

        if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) {
            subscribeUser.execute(userId, type, targetId);
            return Result.free();
        }

        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("Provider required");
        }

        PaymentProviderType providerType;
        try {
            providerType = PaymentProviderType.fromString(provider);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid provider");
        }

        PaymentProviderPort adapter = providers.get(providerType);
        if (adapter == null) {
            throw new IllegalArgumentException("Payment provider not configured: " + providerType);
        }

        PaymentInitResult init = adapter.startCheckout(userId, type, targetId, returnUrl);
        return Result.payment(init);
    }

    private BigDecimal resolvePrice(SubscriptionType type, Long targetId, Long workId) {
        if (type == SubscriptionType.AUTHOR) {
            var authorOpt = loadUserPort.getById(targetId);
            if (authorOpt.isEmpty()) {
                throw new IllegalArgumentException("Author not found");
            }
            User author = authorOpt.get();
            return author.getPrice() == null ? BigDecimal.ZERO : author.getPrice();
        }
        if (type == SubscriptionType.WORK) {
            var workOpt = obtainWorkByIdPort.obtainWorkById(targetId);
            if (workOpt.isEmpty()) {
                throw new IllegalArgumentException("Work not found");
            }
            Work work = workOpt.get();
            return work.getPrice() == null ? BigDecimal.ZERO : work.getPrice();
        }
        if (type == SubscriptionType.CHAPTER) {
            if (workId == null) {
                throw new IllegalArgumentException("workId is required for chapter subscription");
            }
            var chapterOpt = loadChapterPort.loadChapter(workId, targetId);
            if (chapterOpt.isEmpty()) {
                throw new IllegalArgumentException("Chapter does not belong to the specified work");
            }
            Chapter chapter = chapterOpt.get();
            return chapter.getPrice() == null ? BigDecimal.ZERO : chapter.getPrice();
        }
        return BigDecimal.ZERO;
    }

    public static sealed class Result permits Result.FreeResult, Result.PaymentRequiredResult {
        private final boolean free;
        private final PaymentInitResult paymentInit;

        private Result(boolean free, PaymentInitResult paymentInit) {
            this.free = free;
            this.paymentInit = paymentInit;
        }

        public static Result free() { return new FreeResult(); }
        public static Result payment(PaymentInitResult paymentInit) { return new PaymentRequiredResult(paymentInit); }

        public boolean isFree() { return free; }
        public PaymentInitResult getPaymentInit() { return paymentInit; }

        public static final class FreeResult extends Result {
            private FreeResult() { super(true, null); }
        }

        public static final class PaymentRequiredResult extends Result {
            private PaymentRequiredResult(PaymentInitResult paymentInit) { super(false, paymentInit); }
        }
    }
}

