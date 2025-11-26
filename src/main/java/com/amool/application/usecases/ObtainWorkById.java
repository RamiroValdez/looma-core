package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.LikePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;

import java.util.Comparator;
import java.util.Optional;

public class ObtainWorkById {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final FilesStoragePort filesStoragePort;
    private final LikePort likePort;

    public ObtainWorkById(ObtainWorkByIdPort obtainWorkByIdPort,
                          FilesStoragePort filesStoragePort,
                          LikePort likePort) {
        this.likePort = likePort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.filesStoragePort = filesStoragePort;
    }

    public Optional<Work> execute(Long workId, Long userId) {
        return obtainWorkByIdPort.obtainWorkById(workId)
            .map(work -> {
                work.setBanner(this.filesStoragePort.obtainPublicUrl(work.getBanner()));
                work.setCover(this.filesStoragePort.obtainPublicUrl(work.getCover()));
                work.getCategories().sort(Comparator.comparing(Category::getName));
                work.setLikedByUser(likePort.isWorkLikedByUser(work.getId(), userId));
                work.getChapters().forEach(chapter -> {
                    chapter.setLikedByUser(likePort.isChapterLikedByUser(chapter.getId(), userId));
                });

                return work;
            });
    }
}
