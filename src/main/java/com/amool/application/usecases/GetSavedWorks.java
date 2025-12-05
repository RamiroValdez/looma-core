package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.SaveWorkPort;
import com.amool.domain.model.Work;

import java.util.List;
import java.util.stream.Collectors;

public class GetSavedWorks {

    private final SaveWorkPort saveWorkPort;
    private final FilesStoragePort filesStoragePort;

    public GetSavedWorks(SaveWorkPort saveWorkPort, FilesStoragePort filesStoragePort) {
        this.saveWorkPort = saveWorkPort;
        this.filesStoragePort = filesStoragePort;
    }

    public List<Work> execute(Long userId) {
        List<Work> savedWorks = saveWorkPort.getSavedWorksByUser(userId);
        
        return savedWorks.stream()
                .map(this::modifyCoverAndBannerToUrl)
                .collect(Collectors.toList());
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
}