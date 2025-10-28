package com.amool.domain.model;

import java.util.Set;

public class WorkSearchFilter {
    private Set<Long> categoryIds;
    private Set<Long> formatIds;
    private String state;
    private Integer minLikes;
    private String text;
    private String sortBy;
    private Boolean asc = true;

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Set<Long> getFormatIds() {
        return formatIds;
    }

    public void setFormatIds(Set<Long> formatIds) {
        this.formatIds = formatIds;
    }

    public Integer getMinLikes() {
        return minLikes;
    }

    public void setMinLikes(Integer minLikes) {
        this.minLikes = minLikes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
