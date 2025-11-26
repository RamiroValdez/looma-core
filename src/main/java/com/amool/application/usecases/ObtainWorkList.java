package com.amool.application.usecases;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Work;

public class ObtainWorkList {

    private final WorkPort workPort;
    private final FilesStoragePort filesStoragePort;

    public ObtainWorkList(WorkPort workPort, FilesStoragePort filesStoragePort) {
        this.workPort = workPort;
        this.filesStoragePort = filesStoragePort;
    }

    public Map<String, List<Work>> execute(Long userId) {
        Map<String, List<Work>> workList = createWorkListSections(userId);
        
        workList.values().forEach(works -> 
            works.forEach(this::modifyCoverAndBannerToUrl)
        );
        
        return workList;
    }
    
    private Map<String, List<Work>> createWorkListSections(Long userId) {
        Map<String, List<Work>> workList = new HashMap<>();
        if(userId != 0){
            workList.put("currentlyReading", getCurrentlyReading(userId));
        }
        workList.put("topTen", getTopTen());
        workList.put("newReleases", getNewReleases());
        workList.put("recentlyUpdated", getRecentlyUpdated());
        workList.put("userPreferences", getUserPreferences(userId));
        return workList;
    }


    private Work modifyCoverAndBannerToUrl(Work work) {
        try {
            modifyFieldToUrl(work, "cover");
            modifyFieldToUrl(work, "banner");
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
        return work;
    }

    private void modifyFieldToUrl(Work work, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = work.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(work);
        if (value instanceof String) {
            String key = (String) value;
            String publicUrl = filesStoragePort.obtainPublicUrl(key);
            field.set(work, publicUrl);
        }
    }

    private List<Work> getTopTen() {
        return workPort.getAllWorks().stream()
        .filter(this::hasPublishedChapter)
        .sorted((w1, w2) -> Integer.compare(w2.getLikes(), w1.getLikes()))
        .limit(10)
        .collect(Collectors.toList());
    }

    private List<Work> getNewReleases() {
        return workPort.getAllWorks().stream()
        .filter(this::hasPublishedChapter)
        .sorted((w1, w2) -> w2.getPublicationDate().compareTo(w1.getPublicationDate()))
        .limit(20)
        .collect(Collectors.toList());
    }

    private List<Work> getRecentlyUpdated() {
        return workPort.getAllWorks().stream()
        .filter(this::hasPublishedChapter)
        .sorted((w1, w2) -> getLastUpdateDate(w2).compareTo(getLastUpdateDate(w1)))
        .limit(20)
        .collect(Collectors.toList());
    }

    private List<Work> getCurrentlyReading(Long userId) {
        return workPort.getWorksCurrentlyReading(userId);
    }

    private List<Work> getUserPreferences(Long userId) {
            return workPort.getUserPreferences(userId).stream()
                    .filter(this::hasPublishedChapter)
                    .limit(20)
                    .collect(Collectors.toList());
        }

        
    private LocalDateTime getLastUpdateDate(Work work) {
        if (work.getChapters() == null || work.getChapters().isEmpty()) {
            return LocalDateTime.MIN;
        }
        return work.getChapters().stream()
                  .filter(chapter -> chapter.getPublishedAt() != null) 
                  .map(Chapter::getPublishedAt)
                  .max(LocalDateTime::compareTo)
                  .orElse(LocalDateTime.MIN); 
    }

    

    private boolean hasPublishedChapter(Work work) {
        if (work == null || work.getChapters() == null || work.getChapters().isEmpty()) return false;
        return work.getChapters().stream()
                .filter(Objects::nonNull)
                .map(Chapter::getPublicationStatus)
                .filter(Objects::nonNull)
                .anyMatch(status -> "PUBLISHED".equalsIgnoreCase(status));
    }
    
}