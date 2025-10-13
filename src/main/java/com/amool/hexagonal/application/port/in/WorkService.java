package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Work;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Optional;

public interface WorkService {

    Optional<Work> obtainWorkById(Long workId);

    List<Work> getWorksByUserId(Long userId);
    
    List<Work> getAuthenticatedUserWorks(Long authenticatedUserId);

    default Optional<Work> getById(Long workId) {
        return obtainWorkById(workId);
    }

    @Deprecated
    default List<Work> getByCreatorId(Long userId) {
        return getWorksByUserId(userId);
    }

    Long createWork(String title,
                    String description,
                    List<Long> categoryIds,
                    Long formatId,
                    Long originalLanguageId,
                    Set<String> tagIds,
                    MultipartFile coverFile,
                    MultipartFile bannerFile,
                    Long userId) throws IOException;

    void updateCover(Long workId, MultipartFile coverFile, Long authenticatedUserId) throws IOException;

    void updateBanner(Long workId, MultipartFile bannerFile, Long authenticatedUserId) throws IOException;
    
    void deleteWork(Long workId, Long authenticatedUserId) 
        throws SecurityException, NoSuchElementException, RuntimeException;
}
