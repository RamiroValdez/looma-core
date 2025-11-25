package com.amool.application.usecases;

import java.util.Map;

public class ExtractPaymentIdFromWebhook {

    public ExtractPaymentIdResult execute(String id, Map<String, Object> body) {
        String paymentIdStr = id;
        
        if (paymentIdStr == null && body != null) {
            Object dataObj = body.get("data");
            if (dataObj instanceof Map<?, ?> dataMap) {
                Object innerId = dataMap.get("id");
                if (innerId != null) {
                    paymentIdStr = String.valueOf(innerId);
                }
            }
        }
        
        if (paymentIdStr == null && body != null) {
            Object externalRefObj = body.get("externalReference");
            if (externalRefObj instanceof String ref) {
                return ExtractPaymentIdResult.withManualId("manual-" + System.currentTimeMillis(), ref);
            }
        }
        
        if (paymentIdStr == null) {
            return ExtractPaymentIdResult.error("Missing payment id");
        }
        
        return ExtractPaymentIdResult.success(paymentIdStr);
    }
    
    public static class ExtractPaymentIdResult {
        private final String paymentId;
        private final String externalReference;
        private final String errorMessage;
        private final boolean success;
        
        private ExtractPaymentIdResult(String paymentId, String externalReference, String errorMessage, boolean success) {
            this.paymentId = paymentId;
            this.externalReference = externalReference;
            this.errorMessage = errorMessage;
            this.success = success;
        }
        
        public static ExtractPaymentIdResult success(String paymentId) {
            return new ExtractPaymentIdResult(paymentId, null, null, true);
        }
        
        public static ExtractPaymentIdResult withManualId(String paymentId, String externalReference) {
            return new ExtractPaymentIdResult(paymentId, externalReference, null, true);
        }
        
        public static ExtractPaymentIdResult error(String errorMessage) {
            return new ExtractPaymentIdResult(null, null, errorMessage, false);
        }
        
        public String getPaymentId() { return paymentId; }
        public String getExternalReference() { return externalReference; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isSuccess() { return success; }
    }
}
