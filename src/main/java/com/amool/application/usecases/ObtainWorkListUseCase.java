package com.amool.application.usecases;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Work;

public class ObtainWorkListUseCase {

    private final WorkPort workPort;

    public ObtainWorkListUseCase(WorkPort workPort) {
        this.workPort = workPort;
    }


    public Map<String, List<Work>> execute(Long userId) {
        return createWorkListSections(userId);
    }
    
    private Map<String, List<Work>> createWorkListSections(Long userId) {
        Map<String, List<Work>> workList = new HashMap<>();
        workList.put("topTen", getTopTen());
        workList.put("currentlyReading", getCurrentlyReading(userId));
        workList.put("newReleases", getNewReleases());
        workList.put("recentlyUpdated", getRecentlyUpdated());
        return workList;
    }


    private List<Work> getTopTen() {
        return workPort.getAllWorks().stream()
        .sorted((w1, w2) -> Integer.compare(w2.getLikes(), w1.getLikes()))
        .limit(10)
        .collect(Collectors.toList());
    }

    
    private List<Work> getNewReleases() {
        return workPort.getAllWorks().stream()
        .sorted((w1, w2) -> w2.getPublicationDate().compareTo(w1.getPublicationDate()))
        .limit(20)
        .collect(Collectors.toList());
    }
    

    private List<Work> getRecentlyUpdated() {
        return workPort.getAllWorks().stream()
        .sorted((w1, w2) -> getLastUpdateDate(w2).compareTo(getLastUpdateDate(w1)))
        .limit(20)
        .collect(Collectors.toList());
    }

    
    private List<Work> getCurrentlyReading(Long userId) {
        return workPort.getWorksCurrentlyReading(userId);
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
    
}
