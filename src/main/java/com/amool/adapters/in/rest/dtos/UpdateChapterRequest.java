package com.amool.adapters.in.rest.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class UpdateChapterRequest {

    private String title;
    private String status;
    private LocalDateTime last_update;
    private BigDecimal price;
    private Boolean allow_ai_translation;
    private Map<String, String> versions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLast_update() {
        return last_update;
    }

    public void setLast_update(LocalDateTime last_update) {
        this.last_update = last_update;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAllow_ai_translation() {
        return allow_ai_translation;
    }

    public void setAllow_ai_translation(Boolean allow_ai_translation) {
        this.allow_ai_translation = allow_ai_translation;
    }

    public Map<String, String> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, String> versions) {
        this.versions = versions;
    }
}
